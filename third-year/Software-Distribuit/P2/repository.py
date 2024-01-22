
from sqlalchemy.orm import Session
import models, schemas
from sqlalchemy import and_, select
from datetime import datetime


def get_team(db: Session, team_id: int):
    return db.query(models.Team).filter(models.Team.id == team_id).first()


def get_team_by_name(db: Session, name: str):
    return db.query(models.Team).filter(models.Team.name == name).first()


def get_teams(db: Session, skip: int = 0, limit: int = 100):
    return db.query(models.Team).offset(skip).limit(limit).all()

def create_team(db: Session, team: schemas.TeamCreate):
    db_team = models.Team(name=team.name, country=team.country, description=team.description)
    db.add(db_team)
    db.commit()
    db.refresh(db_team)
    return db_team


def update_team(db: Session, team: schemas.Team, team_modify: schemas.TeamCreate):
    team.country = team_modify.country
    team.description = team_modify.description
    db.commit()
    db.refresh(team)
    return team
def delete_team(db: Session, team: schemas.TeamCreate):
    message = team.name
    db.delete(team)
    db.commit()
    return {"message": f"Team {message} ha estat borrat"}

def delete_competition(db: Session, competition: schemas.CompetitionCreate):
    message = competition.name
    db.delete(competition)
    db.commit()
    return {"message": f"Competition {message} ha estat borrat"}

def get_competition(db: Session, competition_id: int):
    return db.query(models.Competition).filter(models.Competition.id == competition_id).first()

def get_competition_by_name(db: Session, name: str):
    return db.query(models.Competition).filter(models.Competition.name == name).first()

def get_competitions(db: Session, skip: int = 0, limit: int = 100):
    return db.query(models.Competition).offset(skip).limit(limit).all()

def create_competition(db: Session, competition: schemas.CompetitionCreate):
    db_competition = models.Competition(name=competition.name, category=competition.category.value, sport=competition.sport.value)
    db.add(db_competition)
    db.commit()
    db.refresh(db_competition)
    return db_competition


def update_competition(db: Session,competition:schemas.Competition,competition_modify:schemas.Competition):
    competition.name = competition_modify.name
    competition.sport = competition_modify.sport
    competition.category = competition_modify.category
    competition.teams.clear()
    for team in competition_modify.teams:
        db_team = db.query(models.Team).filter(models.Team.name == team.name).first()
        if db_team:
            competition.teams.append(db_team)

    db.commit()
    db.refresh(competition)
    return competition


def get_match(db: Session, match_id: int):
    return db.query(models.Match).filter(models.Match.id == match_id).first()


def get_matches(db: Session, skip: int = 0, limit: int = 100):
    return db.query(models.Match).offset(skip).limit(limit).all()

def create_match(db: Session, match: schemas.MatchCreate):
    db_competition = db.query(models.Competition).filter(models.Competition.id == match.competition.id).first()
    db_local = db.query(models.Team).filter(models.Team.id == match.local.id).first()
    db_visitor = db.query(models.Team).filter(models.Team.id == match.visitor.id).first()

    db_match = models.Match(date=match.date, price=match.price, total_available_tickets=match.total_available_tickets)
    db_match.competition = db_competition
    db_match.local = db_local
    db_match.visitor = db_visitor

    db.add(db_match)
    db.commit()
    db.refresh(db_match)
    return db_match
def check_same_matches(db: Session, match: schemas.MatchCreate):
    db_match = db.query(models.Match).filter(
        and_(
            models.Match.date == match.date,
            models.Match.local.has(name=match.local.name),
            models.Match.visitor.has(name=match.visitor.name),
            models.Match.competition.has(name=match.competition.name)
        )
    ).first()

    if db_match:
        return True
    else:
        return False


def delete_match(db: Session, match: schemas.Match):
    message = match.id
    db.delete(match)
    db.commit()
    return {"message": f"Match {message} ha estat borrat"}

def update_match(db: Session, match:schemas.Match, match_modify:schemas.Match):
    match.date = match_modify.date
    match.price = match_modify.price
    db.commit()
    db.refresh(match)
    return match

def get_account(db: Session, username: str):
    return db.query(models.Account).filter(models.Account.username == username).first()

def get_accounts(db: Session, skip: int = 0, limit: int = 100):
    return db.query(models.Account).offset(skip).limit(limit).all()

def create_account(db: Session, account: schemas.AccountCreate):
    db_account = models.Account(username=account.username, available_money=account.available_money, is_admin=account.is_admin)
    db.add(db_account)
    db.commit()
    db.refresh(db_account)
    return db_account


def get_order(db: Session, id: int):
    return db.query(models.Order).filter(models.Order.id == id).first()


def create_order(order: schemas.OrderCreate, username:str, db: Session):
    db_order = models.Order(match_id=order.match_id,tickets_bought=order.tickets_bought)
    account = get_account(db,username)
    match = get_match(db,order.match_id)

    if match.total_available_tickets >= db_order.tickets_bought and account.available_money >= (order.tickets_bought*match.price):
        match.total_available_tickets -= order.tickets_bought
        account.available_money -= order.tickets_bought*match.price
        account.orders.append(db_order)
        db.add(db_order)
        db.commit()
        db.refresh(db_order)
        return db_order

    elif match.total_available_tickets < db_order.tickets_bought:
        return "No hi ha tickets disponibles"

    elif account.available_money < (order.tickets_bought*match.price):
        return "No tens suficients diners"

def get_orders_username(username:str, db: Session):
    account = get_account(db, username)
    return account.orders


def get_orders(db: Session, skip: int = 0, limit: int = 100):
    return db.query(models.Order).offset(skip).limit(limit).all()
