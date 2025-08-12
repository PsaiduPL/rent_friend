import csv
from contextlib import asynccontextmanager
from fastapi import FastAPI, Depends
from sqlalchemy import create_engine, text, Column, Integer, String, desc
from sqlalchemy.orm import Session, declarative_base
from typing import Annotated

# --- Konfiguracja Bazy Danych ---
POSTGRES_URL = "postgresql+psycopg2://user:user@localhost:5433/rentfriend"
engine = create_engine(POSTGRES_URL)

# --- Model SQLAlchemy (ważne, aby pasował do tabeli) ---
Base = declarative_base()


class City(Base):
    __tablename__ = 'cities'
    id = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String, nullable=False)
    population = Column(Integer,nullable=False)


# --- Funkcje pomocnicze do ładowania danych ---

def clear_cities_table(session: Session):
    """Usuwa wszystkie dane z tabeli 'cities'."""
    print("Clearing 'cities' table...")
    # Używamy TRUNCATE, ponieważ jest szybsze niż DELETE dla dużych tabel
    # RESTART IDENTITY resetuje licznik auto-inkrementacji (np. ID)
    session.execute(text("TRUNCATE TABLE cities RESTART IDENTITY CASCADE;"))
    print("Table 'cities' cleared.")


def load_cities_from_csv(session: Session, filepath: str = "data/cleared_cities.csv"):
    """Ładuje dane o miastach z pliku CSV do bazy danych."""
    print(f"Loading data from '{filepath}'...")
    try:
        with open(filepath, mode='r', encoding='utf-8') as csvfile:
            reader = csv.DictReader(csvfile)
            cities_to_add = []
            for row in reader:
                # Zakładamy, że plik CSV ma kolumnę 'name'
                city_name = row.get("name")
                city_population = row.get("population")

                if city_name:
                    cities_to_add.append(City(name=city_name,population=city_population))


            if cities_to_add:
                session.add_all(cities_to_add)
                print(f"Loaded {len(cities_to_add)} cities to the session.")
            else:
                print("No cities found in CSV file.")

    except FileNotFoundError:
        print(f"Error: The file '{filepath}' was not found.")
    except Exception as e:
        print(f"An error occurred while reading the CSV file: {e}")


# --- Zarządzanie cyklem życia aplikacji (startup/shutdown) ---

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Ten kod jest wykonywany przy starcie aplikacji
    print("Application startup: Initializing data...")
    # Tworzenie tabeli, jeśli nie istnieje
    #Base.metadata.create_all(bind=engine)

    # Używamy nowej sesji do operacji startowych
    with Session(engine) as session:
        try:
            # 1. Wyczyść tabelę
            clear_cities_table(session)

            # 2. Załaduj dane z CSV
            load_cities_from_csv(session)

            # 3. Zatwierdź zmiany
            session.commit()
            print("Data initialization complete.")
        except Exception as e:
            print(f"An error occurred during data initialization: {e}")
            session.rollback()  # Wycofaj zmiany w razie błędu

    yield  # Aplikacja działa

    # Ten kod jest wykonywany przy zamykaniu aplikacji (jeśli potrzebne)
    print("Application shutdown.")


# --- Inicjalizacja Aplikacji FastAPI ---
app = FastAPI(lifespan=lifespan)


# --- Zależności (Dependencies) ---

def get_session():
    """Zależność dostarczająca sesję bazy danych do endpointów."""
    with Session(engine) as session:
        yield session


SessionDep = Annotated[Session, Depends(get_session)]


# --- Endpointy API ---

@app.get("/cities/{city}")
async def read_city(city: str, session: SessionDep):
    """Wyszukuje miasta po nazwie z użyciem LIKE."""
    param = f"{city}%"
    # Używamy modelu SQLAlchemy do zapytania - jest to bezpieczniejsza praktyka
    result = session.query(City).filter(City.name.like(param)).order_by(desc(City.population)).all()
    # Konwertujemy wynik na listę słowników
    cities_list = [{"name": c.name} for c in result]
    return {"cities": cities_list}


@app.get("/cities")
async def read_all_cities(session: SessionDep):
    """Zwraca wszystkie miasta z bazy danych."""
    result = session.query(City).order_by(desc(City.population)).all()
    cities_list = [{"name": c.name} for c in result]
    return {"cities": cities_list}
