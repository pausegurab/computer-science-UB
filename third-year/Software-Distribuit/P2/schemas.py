import enum
from pydantic import BaseModel
from typing import Optional
from datetime import datetime




class TeamBase(BaseModel):
    name: str
    country: str
    description: Optional[str] = None


class TeamCreate(TeamBase):
    pass


class Team(TeamBase):
    id: int

    class Config:
        orm_mode = True


class CategoryE(enum.Enum):
    Senior = "Senior"
    Junior = "Junior"


class SportE(enum.Enum):
    Volleyball = "Volleyball"
    Football = "Football"
    Basketball = "Basketball"
    Futsal = "Futsal"


class CompetitionBase(BaseModel):
    name: str
    category: CategoryE
    sport: SportE


class CompetitionCreate(CompetitionBase):
    pass


class Competition(CompetitionBase):
    id: int
    teams: list[Team] = []

    class Config:
        orm_mode = True


class MatchBase(BaseModel):
    date: str
    price: float
    local: Team
    visitor: Team
    competition: Competition
    total_available_tickets: int


class MatchCreate(MatchBase):
    pass


class Match(MatchBase):
    id: int

    class Config:
        orm_mode = True


class OrderBase(BaseModel):
    match_id: int
    tickets_bought: int


class OrderCreate(OrderBase):
    pass


class Order(OrderBase):
    id: int
    username: str

    class Config:
        orm_mode = True
class AccountBase(BaseModel):
    username: str
    password: str
    # 0 not admin/ 1 is admin
    is_admin: int
    available_money: float
    orders: list[Order] = []


class AccountCreate(AccountBase):
    pass


class Account(AccountBase):
    class Config:
        orm_mode = True


