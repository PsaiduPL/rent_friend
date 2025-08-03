import psycopg2
from psycopg2 import OperationalError, ProgrammingError

def create_connection():
    """Tworzy połączenie z bazą danych PostgreSQL."""
    try:
        conn = psycopg2.connect(
            dbname="rentfriend",
            user="user",
            password="user",
            host="localhost",  # lub adres IP Twojego hosta, jeśli Docker działa na innej maszynie
            port="5433"  # Domyślny port PostgreSQL. Zmień, jeśli zmapowałeś na inny w docker-compose.yml. [1, 2, 3]
        )
        print("Połączono z bazą danych PostgreSQL!")
        return conn
    except OperationalError as e:
        print(f"Błąd połączenia: {e}")
        return None

def fetch_all_users(conn):
    """Pobiera wszystkich użytkowników z tabeli 'users'."""
    if not conn:
        return

    cur = conn.cursor()
    try:
        print("\n--- Pobieranie danych z tabeli 'users' ---")
        cur.execute("SELECT * FROM users;")

        # Pobranie nazw kolumn
        colnames = [desc[0] for desc in cur.description]
        print(f"Kolumny: {', '.join(colnames)}")

        users = cur.fetchall() # Pobranie wszystkich wierszy. [4, 5]

        if users:
            print("\nZnaleziono użytkowników:")
            for user in users:
                print(user)
        else:
            print("Tabela 'users' jest pusta.")

    except ProgrammingError as e:
        print(f"Błąd zapytania SQL: {e}")
        print("Upewnij się, że tabela 'users' istnieje w bazie danych 'rentfriend'.")
    finally:
        # Zawsze zamykaj kursor, gdy skończysz
        cur.close()


def main():
    """Główna funkcja programu."""
    conn = create_connection()

    fetch_all_users(conn)

    if conn:
        conn.close()
        print("\nPołączenie z bazą danych zostało zamknięte.")

if __name__ == '__main__':
    main()