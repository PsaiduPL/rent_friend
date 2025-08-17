import csv
from contextlib import asynccontextmanager
from fastapi import FastAPI
from pydantic import BaseModel, Field
from fastapi.middleware.cors import CORSMiddleware
from typing import List
class City(BaseModel):
    name: str
    population: int


CSV_FILEPATH = "data/cleared_cities.csv"


CITIES_IN_MEMORY: List[City] = []

def load_cities_into_memory(filepath: str = CSV_FILEPATH):
    """
    Czyści listę w pamięci i ładuje do niej dane z pliku CSV.
    """
    global CITIES_IN_MEMORY
    CITIES_IN_MEMORY.clear()  # Wyczyść listę na wypadek ponownego załadowania
    print(f"Loading data from '{filepath}' into memory...")

    try:
        with open(filepath, mode='r', encoding='utf-8') as csvfile:
            reader = csv.DictReader(csvfile)
            for row in reader:
                # Sprawdzamy, czy dane istnieją
                if row.get("name") and row.get("population"):
                    # Tworzymy obiekt Pydantic. Automatycznie konwertuje
                    # populację ze stringa na integer i waliduje dane.
                    city_data = City(
                        name=row["name"],
                        population=int(row["population"])
                    )
                    CITIES_IN_MEMORY.append(city_data)

        # Sortujemy listę raz przy starcie, aby wyszukiwanie było szybsze.
        # Wyniki będą już posortowane, więc endpoint nie musi tego robić za każdym razem.
        CITIES_IN_MEMORY.sort(key=lambda city: city.population, reverse=True)
        print(f"Successfully loaded and sorted {len(CITIES_IN_MEMORY)} cities.")

    except FileNotFoundError:
        print(f"ERROR: The file '{filepath}' was not found. The application will run with no data.")
    except Exception as e:
        print(f"An error occurred while loading data: {e}")

    except FileNotFoundError:
        print(f"Error: The file '{filepath}' was not found.")
    except Exception as e:
        print(f"An error occurred while reading the CSV file: {e}")


# --- Zarządzanie cyklem życia aplikacji (startup/shutdown) ---


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Przy starcie aplikacji ładuje dane z CSV do pamięci.
    """
    print("Application startup...")
    load_cities_into_memory()
    yield  # Aplikacja jest gotowa i działa
    print("Application shutdown.")

# --- Inicjalizacja Aplikacji FastAPI ---
app = FastAPI(lifespan=lifespan)

origins = [
    "http://localhost",
    "http://localhost:8080",
    "http://127.0.0.1:5500",  # Częsty port dla rozszerzenia "Live Server" w VS Code
    "null"  # Ważne, aby zezwolić na zapytania z plików otwartych lokalnie (file://)
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],          # <- wszystkie origins
    allow_credentials=True,       # jeśli nie musisz wysyłać cookies, możesz ustawić na False
    allow_methods=["*"],          # wszystkie metody (GET, POST, itd.)
    allow_headers=["*"],          # wszystkie nagłówki
)

# --- Endpointy API ---

@app.get("/cities/{city}")
async def read_city(city: str):

    param = f"{city}%"

    result = filter(lambda x:x.name.startswith(city),CITIES_IN_MEMORY)
    cities_list = [{"name": c.name} for c in result]
    return {"cities": cities_list}


@app.get("/cities")
async def read_all_cities():
    """Zwraca wszystkie miasta z bazy danych."""
    # result = session.query(City).order_by(desc(City.population)).all()
    cities_list = [{"name": c.name} for c in CITIES_IN_MEMORY]
    return {"cities": cities_list}
