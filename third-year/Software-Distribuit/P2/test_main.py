from fastapi.testclient import TestClient
import schemas
import pytest

from main import app

client = TestClient(app)

def test_read_main():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"message": "Hello World"}

def test_read_teams():
    team = {
        "name": "Madrid",
        "country": "Spain",
        "description": "Futbol Sala Team"
    }

    client.post("/teams/",json=team)
    response = client.get("/teams/")
    assert response.status_code == 200
    assert response.json() == [{'country': 'Spain', 'description': 'Futbol Sala Team', 'name': 'Madrid'}]

def test_read_team():
    team = {
        "name": "Madrid",
        "country": "Spain",
        "description": "Futbol Sala Team"
    }

    client.post("/teams/",json=team)
    response = client.get("/team/Madrid")
    assert response.status_code == 200
    assert response.json() == {"team": "Madrid"}

def test_create_team():
    team = {
        "name": "Barça",
        "country": "Spain",
        "description": "FC Barcelona"
    }
    response = client.post("/teams/",json=team)
    assert response.status_code == 200
    assert response.json() == team

def test_delete_team():
    team = {
        "name": "Barça",
        "country": "Spain",
        "description": "FC Barcelona"
    }
    client.post("/teams/",json=team)
    response = client.delete("/teams/Barça")
    assert response.status_code == 200
    assert response.json() == {"message": f"Team Barça has been deleted"}

def test_update_team():
    team1 = {
        "name": "Barça",
        "country": "Spain",
        "description": "FC Barcelona"
    }

    team0 = {
        "name": "Barça",
        "country": "England",
        "description": "FC Barcelona"
    }
    client.put("/teams/",json=team1)
    response = client.put("/teams/",json=team0)
    assert response.status_code == 200
    assert response.json() == {
        "name": "Barça",
        "country": "England",
        "description": "FC Barcelona"
    }

def test_create_competition():
    competition = {
        "id": 0,
        "name": "LaLiga",
        "category": "Senior",
        "sport": "football",
        "teams": []
    }

    response = client.post("/competitions/",json=competition)
    assert response.status_code == 200
    assert response.json() == {
        "id": 0,
        "name": "LaLiga",
        "category": "Senior",
        "sport": "football",
        "teams": []
    }
def test_read_competitions():
    competition = {
        "id": 0,
        "name": "LaLiga",
        "category": "Senior",
        "sport": "football",
        "teams": []
    }

    client.post("/competitions/",json=competition)
    response = client.get("/competitions/")
    assert response.status_code == 200
    assert response.json() == [{
        "id": 0,
        "name": "LaLiga",
        "category": "Senior",
        "sport": "football",
        "teams": []
    }]
def test_read_competition():
    competition = {
        "id": 0,
        "name": "LaLiga",
        "category": "Senior",
        "sport": "football",
        "teams": []
    }

    client.post("/competitions/",json=competition)
    response = client.get("/competition/0")
    assert response.status_code == 200
    assert response.json() == {
        "id": 0,
        "name": "LaLiga",
        "category": "Senior",
        "sport": "football",
        "teams": []
    }

def test_delete_competition():
    competition = {
        "id": 0,
        "name": "LaLiga",
        "category": "Senior",
        "sport": "football",
        "teams": []
    }
    client.post("/competitions/",json=competition)
    response = client.delete("/competitions/0")
    assert response.status_code == 200
    assert response.json() == {"message": f"Competition 0 has been deleted"}
def test_update_competition():
    competition = {
        "id": 0,
        "name": "LaLiga",
        "category": "Senior",
        "sport": "football",
        "teams": []
    }

    competition1 = {
        "id": 0,
        "name": "Premier League",
        "category": "Senior",
        "sport": "football",
        "teams": []
    }
    client.put("/competitions/",json=competition)
    response = client.put("/competitions/",json=competition1)
    assert response.status_code == 200
    assert response.json() == {
        "id": 0,
        "name": "Premier League",
        "category": "Senior",
        "sport": "football",
        "teams": []
    }

def test_create_match():
    match = {
        'id': 0,
        'local': "CV Vall D'Hebron",
        'visitor': 'Volei Rubi',
        'date': '2022-07-03',
        'price': 15.20
    }

    response = client.post("/matches/",json=match)
    assert response.status_code == 200
    assert response.json() == {
        'id': 0,
        'local': "CV Vall D'Hebron",
        'visitor': 'Volei Rubi',
        'date': '2022-07-03',
        'price': 15.20
    }

def test_read_matches():
    match = {
        'id': 0,
        'local': "CV Vall D'Hebron",
        'visitor': 'Volei Rubi',
        'date': '2022-07-03',
        'price': 15.20
    }

    client.post("/matches/",json=match)
    response = client.get("/matches/")
    assert response.status_code == 200
    assert response.json() == [{
        'id': 0,
        'local': "CV Vall D'Hebron",
        'visitor': 'Volei Rubi',
        'date': '2022-07-03',
        'price': 15.20
    }]
def test_read_match():
    match = {
        'id': 0,
        'local': "CV Vall D'Hebron",
        'visitor': 'Volei Rubi',
        'date': '2022-07-03',
        'price': 15.20
    }

    client.post("/matches/",json=match)
    response = client.get("/match/0")
    assert response.status_code == 200
    assert response.json() == {
        'id': 0,
        'local': "CV Vall D'Hebron",
        'visitor': 'Volei Rubi',
        'date': '2022-07-03',
        'price': 15.20
    }
def test_delete_match():
    match = {
        'id': 0,
        'local': "CV Vall D'Hebron",
        'visitor': 'Volei Rubi',
        'date': '2022-07-03',
        'price': 15.20
    }

    client.post("/matches/", json=match)
    response = client.delete("/matches/0")
    assert response.status_code == 200
    assert response.json() == {"message": f"Match 0 has been deleted"}
def test_update_match():
    match = {
        'id': 0,
        'local': "CV Vall D'Hebron",
        'visitor': 'Volei Rubi',
        'date': '2022-07-03',
        'price': 15.20
    }

    match1 = {
        'id': 0,
        'local': "CV Vall D'Hebron",
        'visitor': 'Volei Badalona',
        'date': '2022-07-03',
        'price': 15.20
    }
    client.put("/matches/",json=match)
    response = client.put("/matches/",json=match1)
    assert response.status_code == 200
    assert response.json() == {
        'id': 0,
        'local': "CV Vall D'Hebron",
        'visitor': 'Volei Badalona',
        'date': '2022-07-03',
        'price': 15.20
    }
