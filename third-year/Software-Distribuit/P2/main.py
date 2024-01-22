from fastapi import Depends, FastAPI, HTTPException
from sqlalchemy.orm import Session
import repository, models, schemas
from database import SessionLocal, engine
from fastapi import FastAPI, Request
from fastapi.templating import Jinja2Templates
from fastapi.staticfiles import StaticFiles
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
import models
from typing import Union

models.Base.metadata.create_all(bind=engine)  # Creem la base de dades amb els models que hem definit a SQLAlchemy

app = FastAPI()

app.mount("/static", StaticFiles(directory="frontend/dist/static"), name="static")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)



# Dependency to get a DB session
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


templates = Jinja2Templates(directory="frontend/dist")


@app.get("/")
async def serve_index(request: Request):
    return templates.TemplateResponse("index.html", {"request": request})

@app.get("/")
async def main():
    return {"message": "Hello World"}
# GET Teams
@app.get("/teams/", response_model=list[schemas.Team])
def read_teams(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    return repository.get_teams(db, skip=skip, limit=limit)


# GET Teams (by name)
@app.get("/teams/{team_name}", response_model=schemas.Team)
def read_team(team_name: str, db: Session = Depends(get_db)):
    team = repository.get_team_by_name(db, name=team_name)
    if not team:
        raise HTTPException(status_code=404, detail="Team not found")
    return team

# POST Teams
@app.post("/teams/", response_model=schemas.Team)
def create_team(team: schemas.TeamCreate, db: Session = Depends(get_db)):
    db_team = repository.get_team_by_name(db, name=team.name)
    if db_team:
        raise HTTPException(status_code=400, detail="Team already Exists, Use put for updating")
    else:
        return repository.create_team(db=db, team=team)


# PUT Teams
@app.put("/teams/{team_id}", response_model=schemas.Team)
def update_team(team_id: int, team: schemas.Team, db: Session = Depends(get_db)):
    db_team = repository.get_team(db, team_id)
    if db_team:
        return repository.update_team(db=db, team=db_team, team_modify=team)
    else:
        raise HTTPException(status_code=400, detail="Team doesn't Exist, Use post for creating")


# DELETE Team
@app.delete("/teams/{team_name}", response_model=dict)
def delete_team(team_name: str, db: Session = Depends(get_db)):
    team = repository.get_team_by_name(db, name=team_name)
    if not team:
        raise HTTPException(status_code=404, detail="Team not found")
    else:
        return repository.delete_team(db, team)


# GET Competitions
@app.get("/competitions/", response_model=list[schemas.Competition])
def read_competitions(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    return repository.get_competitions(db, skip=skip, limit=limit)


# GET Competitions (by name)
@app.get("/competitions/{competition_name}", response_model=schemas.Competition)
def read_competition_by_name(competition_name: str, db: Session = Depends(get_db)):
    competition = repository.get_competition_by_name(db, name=competition_name)
    if not competition:
        raise HTTPException(status_code=404, detail="Competition not found")
    return competition


# POST Competitions
@app.post("/competitions/", response_model=schemas.Competition)
def create_competition(competition: schemas.CompetitionCreate, db: Session = Depends(get_db)):
    db_competition = repository.get_competition_by_name(db, name=competition.name)
    if db_competition:
        raise HTTPException(status_code=400, detail="Competition already Exists, Use put for updating")
    else:
        return repository.create_competition(db=db, competition=competition)


# PUT Competitions
@app.put("/competitions/{competition_id}", response_model=schemas.Competition)
def update_competiton(competition_id: int, competition: schemas.Competition, db: Session = Depends(get_db)):
    db_competition = repository.get_competition(db, competition_id)
    if db_competition:
        return repository.update_competition(db=db, competition=db_competition, competition_modify=competition)
    else:
        raise HTTPException(status_code=400, detail="Competition doesn't exist, Use post for creating")


# DELETE Competitions
@app.delete("/competitions/{competition_name}", response_model=dict)
def delete_competition(competition_name: str, db: Session = Depends(get_db)):
    competition = repository.get_competition_by_name(db, name=competition_name)
    if not competition:
        raise HTTPException(status_code=404, detail="Competition not found")
    else:
        return repository.delete_competition(db, competition)


# GET Matches
@app.get("/matches/", response_model=list[schemas.Match])
def read_matches(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    return repository.get_matches(db, skip=skip, limit=limit)


# POST Matches
@app.post("/matches/", response_model=schemas.Match)
def create_match(match: schemas.MatchCreate, db: Session = Depends(get_db)):
    db_match = repository.check_same_matches(db, match)
    if db_match:
        raise HTTPException(status_code=400, detail="Match already Exists, Use put for updating")
    else:
        return repository.create_match(db=db, match=match)


# PUT Matches
@app.put("/matches/{match_id}", response_model=schemas.Match)
def update_competiton(match_id: int, match: schemas.Match, db: Session = Depends(get_db)):
    db_match = repository.get_match(db, match_id)
    if db_match:
        return repository.update_match(db, db_match, match)
    else:
        raise HTTPException(status_code=400, detail="Match doesn't exist, Use post for creating")


# DELETE Matches
@app.delete("/matches/{match_id}", response_model=dict)
def delete_match(match_id: int, db: Session = Depends(get_db)):
    match = repository.get_match(db, match_id=match_id)
    if not match:
        raise HTTPException(status_code=404, detail="Team not found")
    else:
        return repository.delete_match(db=db, match=match)

@app.post('/account/', response_model=schemas.Account)
def post_account(account: schemas.AccountCreate, db: Session = Depends(get_db)):
    db_account = repository.get_account(db, account.username)
    if db_account:
        raise HTTPException(status_code=400, detail="Account already Exists, Use put for updating")
    else:
        return repository.create_account(db=db, account=account)


@app.get("/accounts/", response_model=list[schemas.Account])
def read_accounts(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    return repository.get_accounts(db, skip=skip, limit=limit)

@app.get("/account/{username}", response_model=schemas.Account)
def read_account(username: str, db: Session = Depends(get_db)):
    return repository.get_account(db, username=username)

@app.post('/orders/{username}', response_model=Union[schemas.Order,str])
def post_order(order:schemas.OrderCreate,username:str,db: Session = Depends(get_db)):
    db_order = repository.create_order(order=order,username=username,db=db)
    return db_order



@app.get('/orders/{username}', response_model=list[schemas.Order])
def get_orders_username(username:str,db: Session = Depends(get_db)):
    return repository.get_orders_username(username=username, db=db)




@app.get('/orders', response_model=list[schemas.Order])
def get_orders(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    return repository.get_orders(db, skip=skip, limit=limit)