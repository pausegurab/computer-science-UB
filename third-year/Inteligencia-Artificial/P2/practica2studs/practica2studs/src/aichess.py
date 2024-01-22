#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Sep  8 11:22:03 2022

@author: ignasi
"""
import copy
import math

import chess
import board
import numpy as np
import sys
import queue
from typing import List

RawStateType = List[List[List[int]]]

from itertools import permutations


class Aichess():
    """
    A class to represent the game of chess.

    ...

    Attributes:
    -----------
    chess : Chess
        represents the chess game

    Methods:
    --------
    startGame(pos:stup) -> None
        Promotes a pawn that has reached the other side to another, or the same, piece

    """

    def __init__(self, TA, myinit=True):

        if myinit:
            self.chess = chess.Chess(TA, True)
        else:
            self.chess = chess.Chess([], False)

        self.listNextStates = []
        self.listVisitedStates = []
        self.listVisitedSituations = []
        self.pathToTarget = []
        self.currentStateW = self.chess.boardSim.currentStateW;
        self.depthMax = 8;
        self.checkMate = False
        self.estatCost = {}
        self.turn = True

    def copyState(self, state):

        copyState = []
        for piece in state:
            copyState.append(piece.copy())
        return copyState

    def isVisitedSituation(self, color, mystate):

        if (len(self.listVisitedSituations) > 0):
            perm_state = list(permutations(mystate))

            isVisited = False
            for j in range(len(perm_state)):

                for k in range(len(self.listVisitedSituations)):
                    if self.isSameState(list(perm_state[j]), self.listVisitedSituations.__getitem__(k)[1]) and color == \
                            self.listVisitedSituations.__getitem__(k)[0]:
                        isVisited = True

            return isVisited
        else:
            return False

    def getCurrentStateW(self):

        return self.myCurrentStateW

    def getListNextStatesW(self, myState):

        self.chess.boardSim.getListNextStatesW(myState)
        self.listNextStates = self.chess.boardSim.listNextStates.copy()

        return self.listNextStates

    def getListNextStatesB(self, myState):
        self.chess.boardSim.getListNextStatesB(myState)
        self.listNextStates = self.chess.boardSim.listNextStates.copy()

        return self.listNextStates

    def isSameState(self, a, b):

        isSameState1 = True
        # a and b are lists
        for k in range(len(a)):

            if a[k] not in b:
                isSameState1 = False

        isSameState2 = True
        # a and b are lists
        for k in range(len(b)):

            if b[k] not in a:
                isSameState2 = False

        isSameState = isSameState1 and isSameState2
        return isSameState

    def isVisited(self, mystate):

        if (len(self.listVisitedStates) > 0):
            perm_state = list(permutations(mystate))

            isVisited = False
            for j in range(len(perm_state)):

                for k in range(len(self.listVisitedStates)):

                    if self.isSameState(list(perm_state[j]), self.listVisitedStates[k]):
                        isVisited = True

            return isVisited
        else:
            return False

    def isWatchedBk(self, currentState):

        self.newBoardSim(currentState)

        bkPosition = self.getPieceState(currentState, 12)[0:2]
        wkState = self.getPieceState(currentState, 6)
        wrState = self.getPieceState(currentState, 2)
        # Si les negres maten el rei blanc, no és una configuració correcta
        if wkState == None:
            return False
        # Mirem les possibles posicions del rei blanc i mirem si en alguna pot "matar" al rei negre
        for wkPosition in self.getNextPositions(wkState):
            if bkPosition == wkPosition:
                #         # Tindríem un checkMate
                return True
        if wrState != None:
            # Mirem les possibles posicions de la torre blanca i mirem si en alguna pot "matar" al rei negre
            for wrPosition in self.getNextPositions(wrState):
                if bkPosition == wrPosition:
                    return True

        return False

    def isWatchedWk(self, currentState):
        self.newBoardSim(currentState)

        wkPosition = self.getPieceState(currentState, 6)[0:2]
        bkState = self.getPieceState(currentState, 12)
        brState = self.getPieceState(currentState, 8)

        # If whites kill the black king , it is not a correct configuration
        if bkState == None:
            return False
        # We check all possible positions for the black king, and chck if in any of them it may kill the white king
        for bkPosition in self.getNextPositions(bkState):
            if wkPosition == bkPosition:
                # That would be checkMate
                return True
        if brState != None:
            # We check the possible positions of the black tower, and we chck if in any o them it may killt he white king
            for brPosition in self.getNextPositions(brState):
                if wkPosition == brPosition:
                    return True

        return False

    def newBoardSim(self, listStates):
        # We create a  new boardSim
        TA = np.zeros((8, 8))
        for state in listStates:
            TA[state[0]][state[1]] = state[2]

        self.chess.newBoardSim(TA)

    def getPieceState(self, state, piece):
        pieceState = None
        for i in state:
            if i[2] == piece:
                pieceState = i
                break
        return pieceState

    def getCurrentState(self):
        listStates = []
        for i in self.chess.board.currentStateW:
            listStates.append(i)
        for j in self.chess.board.currentStateB:
            listStates.append(j)
        return listStates

    def getNextPositions(self, state):
        # Given a state, we check the next possible states
        # From these, we return a list with position, i.e., [row, column]
        if state == None:
            return None
        if state[2] > 6:
            nextStates = self.getListNextStatesB([state])
        else:
            nextStates = self.getListNextStatesW([state])
        nextPositions = []
        for i in nextStates:
            nextPositions.append(i[0][0:2])
        return nextPositions

    def getWhiteState(self, currentState):
        whiteState = []
        wkState = self.getPieceState(currentState, 6)
        whiteState.append(wkState)
        wrState = self.getPieceState(currentState, 2)
        if wrState != None:
            whiteState.append(wrState)
        return whiteState

    def getBlackState(self, currentState):
        blackState = []
        bkState = self.getPieceState(currentState, 12)
        blackState.append(bkState)
        brState = self.getPieceState(currentState, 8)
        if brState != None:
            blackState.append(brState)
        return blackState

    def getMovement(self, state, nextState):
        # Given a state and a successor state, return the postiion of the piece that has been moved in both states
        pieceState = None
        pieceNextState = None
        for piece in state:
            if piece not in nextState:
                movedPiece = piece[2]
                pieceNext = self.getPieceState(nextState, movedPiece)
                if pieceNext != None:
                    pieceState = piece
                    pieceNextState = pieceNext
                    break

        return [pieceState, pieceNextState]

    def heuristica(self, currentState, color):
        # In this method, we calculate the heuristics for both the whites and black ones
        # The value calculated here is for the whites,
        # but finally from verything, as a function of the color parameter, we multiply the result by -1
        value = 0

        bkState = self.getPieceState(currentState, 12)
        wkState = self.getPieceState(currentState, 6)
        wrState = self.getPieceState(currentState, 2)
        brState = self.getPieceState(currentState, 8)

        filaBk = bkState[0]
        columnaBk = bkState[1]
        filaWk = wkState[0]
        columnaWk = wkState[1]

        if wrState != None:
            filaWr = wrState[0]
            columnaWr = wrState[1]
        if brState != None:
            filaBr = brState[0]
            columnaBr = brState[1]

        # We check if they killed the black tower
        if brState == None:
            value += 50
            fila = abs(filaBk - filaWk)
            columna = abs(columnaWk - columnaBk)
            distReis = min(fila, columna) + abs(fila - columna)
            if distReis >= 3 and wrState != None:
                filaR = abs(filaBk - filaWr)
                columnaR = abs(columnaWr - columnaBk)
                value += (min(filaR, columnaR) + abs(filaR - columnaR)) / 10
            # If we are white white, the closer our king from the oponent, the better
            # we substract 7 to the distance between the two kings, since the max distance they can be at in a board is 7 moves
            value += (7 - distReis)
            # If they black king is against a wall, we prioritize him to be at a corner, precisely to corner him
            if bkState[0] == 0 or bkState[0] == 7 or bkState[1] == 0 or bkState[1] == 7:
                value += (abs(filaBk - 3.5) + abs(columnaBk - 3.5)) * 10
            # If not, we will only prioritize that he approahces the wall, to be able to approach the check mate
            else:
                value += (max(abs(filaBk - 3.5), abs(columnaBk - 3.5))) * 10

        # They killed the black tower. Within this method, we consider the same conditions than in the previous condition
        # Within this method we consider the same conditions than in the previous section, but now with reversed values.
        if wrState == None:
            value += -50
            fila = abs(filaBk - filaWk)
            columna = abs(columnaWk - columnaBk)
            distReis = min(fila, columna) + abs(fila - columna)

            if distReis >= 3 and brState != None:
                filaR = abs(filaWk - filaBr)
                columnaR = abs(columnaBr - columnaWk)
                value -= (min(filaR, columnaR) + abs(filaR - columnaR)) / 10
            # If we are white, the close we have our king from the oponent, the better
            # If we substract 7 to the distance between both kings, as this is the max distance they can be at in a chess board
            value += (-7 + distReis)

            if wkState[0] == 0 or wkState[0] == 7 or wkState[1] == 0 or wkState[1] == 7:
                value -= (abs(filaWk - 3.5) + abs(columnaWk - 3.5)) * 10
            else:
                value -= (max(abs(filaWk - 3.5), abs(columnaWk - 3.5))) * 10

        # We are checking blacks
        if self.isWatchedBk(currentState):
            value += 20

        # We are checking whites
        if self.isWatchedWk(currentState):
            value += -20

        # If black, values are negative, otherwise positive
        if not color:
            value = (-1) * value

        return value

    def isCheckmateReal(self):
        #Comprovem el checkmate al tauler real
        currentState = self.getCurrentState()
        white = self.isCheckmateW(currentState)
        black = self.isCheckmateB(currentState)
        return black or white

    def isCheckmate(self):
        # Comprovem el checkmate al tauler simulat
        currentState = self.getCurrentStateSim()
        white = self.isCheckmateW(currentState)
        black = self.isCheckmateB(currentState)
        return black or white

    def isCheckmateW(self, currentState):
        wState = self.getWhiteState(currentState)
        if self.isWatchedWk(currentState):
            for s in self.getListNextStatesW(wState):
                estado = self.calcularEstatSeguent(currentState, s)
                if not self.isWatchedWk(estado):
                    return False
            return True
        return False

    def isCheckmateB(self, currentState):

        bState = self.getBlackState(currentState)
        if self.isWatchedBk(currentState):
            for s in self.getListNextStatesB(bState):
                estado = self.calcularEstatSeguent(currentState, s)
                if (not self.isWatchedBk(estado)):
                    return False
            return True
        return False

    def minimaxGame(self, depthWhite, depthBlack):
        self.turn = True
        currentState = self.getCurrentState()
        iteraciones = 0
        while (not self.isCheckmateReal() and (iteraciones < 50)):
            if self.turn:
                v, nuevoEstado = self.max_value(currentState, depthWhite)
                nuevoEstadoDefinitivo = self.calcularEstatSeguent(currentState, self.getWhiteState(nuevoEstado))
            else:
                v, nuevoEstado = self.max_value(currentState, depthBlack)
                nuevoEstadoDefinitivo = self.calcularEstatSeguent(currentState, self.getBlackState(nuevoEstado))
            move = self.getMovement(currentState, nuevoEstadoDefinitivo)

            self.chess.move(move[0], move[1])
            self.chess.board.print_board()
            currentState = self.getCurrentState()
            print("current State", currentState)
            print("LA V: ", v)
            self.turn = not self.turn
            iteraciones += 1
            print("ITERACION NUMERO: ", iteraciones)

        return
        # Your code here

    def max_value(self, currentState, depth):
        v = float('-inf')
        estado_maximo = None
        self.newBoardSim(currentState)
        #Comprovem si es un estat terminal
        if depth <= 0 or self.isCheckmate():
            return self.heuristica(currentState, True), currentState
        self.newBoardSim(currentState)
        #En cas que no ho sigui mirem si toca moure a les blanques o a les negres
        if self.turn:
            #Busquem els posibles nous estats de les figures blanques
            estadosW = self.getListNextStatesW(self.getWhiteState(currentState))
            for estatsW in estadosW:
                #Creem el estat sencer per cada posible estat de Blanques
                estado = self.calcularEstatSeguent(currentState, estatsW)

                if self.isWatchedWk(estado):
                    #Si posem el rei en perill no contemplem l'estat
                    continue
                if estado_maximo == None:
                    #Guardem un estat per no retornar estat = None
                    estado_maximo = estado
                self.newBoardSim(currentState)
                #Crida a la funció min_value
                val, newState = self.min_value(estado, depth - 1)
                self.newBoardSim(currentState)
                if newState == None:
                    #Mirem si ens retorna None que vol dir que és un estat final d'empat.
                    # empate
                    val = 0
                    estado_maximo = estado
                if val > v:
                    #Comprovem si es un estat millor que el anterior.
                    v = val
                    estado_maximo = estado
        # Torn de les negres
        else:
            #Busquem els posibles nous estats de les figures negres
            estadosB = self.getListNextStatesB(self.getBlackState(currentState))
            for estatsB in estadosB:
                # Creem el estat sencer per cada posible estat de Negres
                estado = self.calcularEstatSeguent(currentState, estatsB)
                if self.isWatchedBk(estado):
                    # Si posem el rei en perill no contemplem l'estat
                    continue
                if estado_maximo == None:
                    # Guardem un estat per no retornar estat = None
                    estado_maximo = estado
                self.newBoardSim(currentState)
                # Crida a la funció min_value
                val, newState = self.min_value(estado, depth - 1)
                self.newBoardSim(currentState)
                if newState == None:
                    # Mirem si ens retorna None que vol dir que és un estat final d'empat.
                    # empate
                    val = 0
                    estado_maximo = estado
                if val > v:
                    # Comprovem si es un estat millor que el anterior.
                    v = val
                    estado_maximo = estado

        #Retornem l'evaluació i l'estat.
        return v, estado_maximo

    def min_value(self, currentState, depth):
        v = float('inf')
        estado_minimo = None
        self.newBoardSim(currentState)
        # Comprovem si es un estat terminal
        if depth <= 0 or self.isCheckmate():
            return self.heuristica(currentState, True), currentState
        self.newBoardSim(currentState)
        # En cas que no ho sigui mirem si toca moure a les blanques o a les negres
        if self.turn:
            # Busquem els posibles nous estats de les figures Negres
            estadosB = self.getListNextStatesB(self.getBlackState(currentState))

            for estatsB in estadosB:
                # Creem el estat sencer per cada posible estat de Negres
                estado = self.calcularEstatSeguent(currentState, estatsB)
                if self.isWatchedBk(estado):
                    # Si posem el rei en perill no contemplem l'estat
                    continue
                if estado_minimo == None:
                    # Guardem un estat per no retornar estat = None
                    estado_minimo = estado
                self.newBoardSim(currentState)
                # Crida a la funció max_value
                val, newState = self.max_value(estado, depth - 1)
                self.newBoardSim(currentState)
                if newState == None:
                    # Mirem si ens retorna None que vol dir que és un estat final d'empat.
                    # empate
                    val = 0
                    estado_minimo = estado
                if val < v:
                    # Comprovem si es un estat millor que el anterior.
                    v = val
                    estado_minimo = estado
        #Torn de blanques al min
        else:
            # Busquem els posibles nous estats de les figures blanques
            estadosW = self.getListNextStatesW(self.getWhiteState(currentState))
            for estatsW in estadosW:
                # Creem el estat sencer per cada posible estat de Blanques
                estado = self.calcularEstatSeguent(currentState, estatsW)

                if self.isWatchedWk(estado):
                    # Si posem el rei en perill no contemplem l'estat
                    continue
                if estado_minimo == None:
                    # Guardem un estat per no retornar estat = None
                    estado_minimo = estado
                self.newBoardSim(currentState)
                # Crida a la funció max_value
                val, newState = self.max_value(estado, depth - 1)
                self.newBoardSim(currentState)
                if newState == None:
                    # Mirem si ens retorna None que vol dir que és un estat final d'empat.
                    # empate
                    val = 0
                    estado_minimo = estado
                if val < v:
                    # Comprovem si es un estat millor que el anterior.
                    v = val
                    estado_minimo = estado
        #retornem l'evaluació i el estat minim
        return v, estado_minimo

    def calcularEstatSeguent(self, currentState, estats):
        #Metode per trobar el seguent estat a partir del currentState i el nou estat de les peces d'un color només
        estatSeguent = estats.copy()
        prova = []
        for a in currentState:
            prova.append(a)

        for estats in estatSeguent:
            for i in currentState:
                if (estats[0:2] == i[0:2]) and (estats[2] != i[2]):
                    prova.remove(i)
                if (estats[2] == i[2]) and (estats[0:2] != i[0:2]):
                    prova.remove(i)
                    prova.append(estats)
        return prova

    def getCurrentStateSim(self):
        #Metode per obtenir l'estat del tauler simulat
        listStates = []
        for i in self.chess.boardSim.currentStateW:
            listStates.append(i)
        for j in self.chess.boardSim.currentStateB:
            listStates.append(j)
        return listStates

    def alphaBetaPoda(self, depthWhite, depthBlack):
        self.turn = True
        currentState = self.getCurrentState()
        iteraciones = 0
        alpha = float('-inf')
        beta =  float('inf')

        while (not self.isCheckmateReal() and (iteraciones < 50)):
            if self.turn:
                v, nuevoEstado = self.max_value_poda(currentState, depthWhite, alpha, beta)
                nuevoEstadoDefinitivo = self.calcularEstatSeguent(currentState, self.getWhiteState(nuevoEstado))
            else:
                v, nuevoEstado = self.max_value_poda(currentState, depthBlack, alpha, beta)
                nuevoEstadoDefinitivo = self.calcularEstatSeguent(currentState, self.getBlackState(nuevoEstado))

            move = self.getMovement(currentState, nuevoEstadoDefinitivo)

            self.chess.move(move[0], move[1])
            self.chess.board.print_board()
            currentState = self.getCurrentState()
            print("current State", currentState)
            print("LA V: ", v)
            self.turn = not self.turn
            iteraciones += 1
            print("ITERACION NUMERO: ", iteraciones)

        return
        # Your code here

    def max_value_poda(self, currentState, depth, alpha, beta):
        v = float('-inf')
        estado_maximo = None
        self.newBoardSim(currentState)
        # Comprovem si es un estat terminal
        if depth <= 0 or self.isCheckmate():
            return self.heuristica(currentState, True), currentState
        self.newBoardSim(currentState)
        # En cas que no ho sigui mirem si toca moure a les blanques o a les negres
        if self.turn:
            # Busquem els posibles nous estats de les figures blanques
            estadosW = self.getListNextStatesW(self.getWhiteState(currentState))
            for estatsW in estadosW:
                # Creem el estat sencer per cada posible estat de Blanques
                estado = self.calcularEstatSeguent(currentState, estatsW)
                if self.isWatchedWk(estado):
                    # Si posem el rei en perill no contemplem l'estat
                    continue
                if estado_maximo == None:
                    # Guardem un estat per no retornar estat = None
                    estado_maximo = estado
                self.newBoardSim(currentState)
                # Crida a la funció min_value_poda
                valor, newState = self.min_value_poda(estado, depth-1, alpha, beta)
                if newState == None:
                    # Mirem si ens retorna None que vol dir que és un estat final d'empat.
                    # empate
                    valor = 0
                    estado_maximo = estado
                if v < valor:
                    # Comprovem si es un estat millor que el anterior.
                    v = valor
                    estado_maximo = estado
                alpha = max(alpha, v)
                if beta <= alpha:
                    #Realitzem la PODA
                    break

                self.newBoardSim(currentState)
        # Torn de les negres
        else:
            # Busquem els posibles nous estats de les figures negres
            estadosB = self.getListNextStatesB(self.getBlackState(currentState))
            for estatsB in estadosB:
                # Creem el estat sencer per cada posible estat de Negres
                estado = self.calcularEstatSeguent(currentState, estatsB)
                if self.isWatchedBk(estado):
                    # Si posem el rei en perill no contemplem l'estat
                    continue
                if estado_maximo == None:
                    # Guardem un estat per no retornar estat = None
                    estado_maximo = estado
                self.newBoardSim(currentState)
                # Crida a la funció min_value_poda
                valor, newState = self.min_value_poda(estado, depth - 1, alpha, beta)
                if newState == None:
                    # Mirem si ens retorna None que vol dir que és un estat final d'empat.
                    # empate
                    valor = 0
                    estado_maximo = None
                if v < valor:
                    # Comprovem si es un estat millor que el anterior.
                    v = valor
                    estado_maximo = estado
                alpha = max(alpha, v)
                if beta <= alpha:
                    #Realitzem la PODA
                    break

                self.newBoardSim(currentState)
        # retornem l'evaluació i l'estat màxim
        return v, estado_maximo

    def min_value_poda(self, currentState, depth, alpha, beta):
        v = float('inf')
        estado_minimo = None
        self.newBoardSim(currentState)
        # Comprovem si es un estat terminal
        if depth <= 0 or self.isCheckmate():
            return self.heuristica(currentState, True), currentState
        self.newBoardSim(currentState)
        # En cas que no ho sigui mirem si toca moure a les blanques o a les negres
        if self.turn:
            # Busquem els posibles nous estats de les figures negres
            estadosB = self.getListNextStatesB(self.getBlackState(currentState))

            for estatsB in estadosB:
                # Creem el estat sencer per cada posible estat de Negres
                estado = self.calcularEstatSeguent(currentState, estatsB)

                if self.isWatchedBk(estado):
                    # Si posem el rei en perill no contemplem l'estat
                    continue
                if estado_minimo == None:
                    # Guardem un estat per no retornar estat = None
                    estado_minimo = estado
                self.newBoardSim(currentState)
                # Crida a la funció max_value
                valor, newState = self.max_value_poda(estado, depth - 1, alpha, beta)
                if newState == None:
                    # Mirem si ens retorna None que vol dir que és un estat final d'empat.
                    valor = 0
                    estado_minimo = None
                if v > valor:
                    # Comprovem si es un estat millor que el anterior.
                    v = valor
                    estado_minimo = estado
                beta = min(beta, v)
                if beta <= alpha:
                    #Realitzem la poda
                    break
                self.newBoardSim(currentState)
        # Torn de les blanques
        else:
            # Busquem els posibles nous estats de les figures blanques
            estadosW = self.getListNextStatesW(self.getWhiteState(currentState))
            for estatsW in estadosW:
                # Creem el estat sencer per cada posible estat de Blanques
                estado = self.calcularEstatSeguent(currentState, estatsW)

                if self.isWatchedWk(estado):
                    # Si posem el rei en perill no contemplem l'estat
                    continue
                if estado_minimo == None:
                    # Guardem un estat per no retornar estat = None
                    estado_minimo = estado
                self.newBoardSim(currentState)
                # Crida a la funció max_value_poda
                valor, newState = self.max_value_poda(estado, depth - 1, alpha, beta)

                if newState == None:
                    # Mirem si ens retorna None que vol dir que és un estat final d'empat.
                    # empate
                    valor = 0
                    estado_minimo = None
                if v > valor:
                    # Comprovem si es un estat millor que el anterior.
                    v = valor
                    estado_minimo = estado
                beta = min(beta, v)
                if beta <= alpha:
                    # realitzem la PODA
                    break

                self.newBoardSim(currentState)
        #Retornem l'evaluació i l'estat.
        return v, estado_minimo

    def exercici3(self, depthWhite, depthBlack):
        self.turn = True
        currentState = self.getCurrentState()
        iteraciones = 0
        alpha = float('-inf')
        beta =  float('inf')
        while (not self.isCheckmateReal() and (iteraciones < 50)):
            if self.turn:
                v, nuevoEstado = self.max_value(currentState, depthWhite)
                nuevoEstadoDefinitivo = self.calcularEstatSeguent(currentState, self.getWhiteState(nuevoEstado))
            else:
                v, nuevoEstado = self.max_value_poda(currentState, depthBlack, alpha, beta)
                nuevoEstadoDefinitivo = self.calcularEstatSeguent(currentState, self.getBlackState(nuevoEstado))

            move = self.getMovement(currentState, nuevoEstadoDefinitivo)

            self.chess.move(move[0], move[1])
            self.chess.board.print_board()
            currentState = self.getCurrentState()
            print("current State", currentState)
            print("LA V: ", v)
            self.turn = not self.turn
            iteraciones += 1
            print("ITERACION NUMERO: ", iteraciones)

        return


    def expectimax(self, depthWhite, depthBlack):
        self.turn = True
        currentState = self.getCurrentState()
        iteraciones = 0
        alpha = float('-inf')
        beta = float('inf')
        while (not self.isCheckmateReal() and (iteraciones < 50)):
            if self.turn:
                v, nuevoEstado = self.max_value_expectimax(currentState, depthWhite)
                nuevoEstadoDefinitivo = self.calcularEstatSeguent(currentState, self.getWhiteState(nuevoEstado))
            else:
                v, nuevoEstado = self.max_value_expectimax(currentState, depthBlack)
                nuevoEstadoDefinitivo = self.calcularEstatSeguent(currentState, self.getBlackState(nuevoEstado))

            move = self.getMovement(currentState, nuevoEstadoDefinitivo)

            self.chess.move(move[0], move[1])
            self.chess.board.print_board()
            currentState = self.getCurrentState()
            print("current State", currentState)
            print("LA V: ", v)
            self.turn = not self.turn
            iteraciones += 1
            print("ITERACION NUMERO: ", iteraciones)
        # Your code here
        return

    def max_value_expectimax(self, currentState, depth):
        v = float('-inf')
        estado_maximo = None
        self.newBoardSim(currentState)
        # Comprovem si es un estat terminal
        if depth <= 0 or self.isCheckmate():
            return self.heuristica(currentState, True), currentState
        self.newBoardSim(currentState)
        # En cas que no ho sigui mirem si toca moure a les blanques o a les negres
        if self.turn:
            # Busquem els posibles nous estats de les figures blanques
            estadosW = self.getListNextStatesW(self.getWhiteState(currentState))
            for estatsW in estadosW:
                # Creem el estat sencer per cada posible estat de Blanques
                estado = self.calcularEstatSeguent(currentState, estatsW)

                if self.isWatchedWk(estado):
                    # Si posem el rei en perill no contemplem l'estat
                    continue
                if estado_maximo == None:
                    # Guardem un estat per no retornar estat = None
                    estado_maximo = estado
                self.newBoardSim(currentState)
                # Crida a la funció min_value_expectimax
                val = self.min_value_expectimax(estado, depth - 1)
                #self.newBoardSim(currentState)
                if val > v:
                    # Comprovem si es un estat millor que el anterior.
                    v = val
                    estado_maximo = estado
        # Torn de les negres
        else:
            # Busquem els posibles nous estats de les figures negres
            estadosB = self.getListNextStatesB(self.getBlackState(currentState))
            for estatsB in estadosB:
                # Creem el estat sencer per cada posible estat de Negres
                estado = self.calcularEstatSeguent(currentState, estatsB)
                if self.isWatchedBk(estado):
                    # Si posem el rei en perill no contemplem l'estat
                    continue
                if estado_maximo == None:
                    # Guardem un estat per no retornar estat = None
                    estado_maximo = estado
                self.newBoardSim(currentState)
                # Crida a la funció min_value_expectimax
                val = self.min_value_expectimax(estado, depth - 1)
                #self.newBoardSim(currentState)
                if val > v:
                    # Comprovem si es un estat millor que el anterior.
                    v = val
                    estado_maximo = estado
        # Retornem l'evaluació i l'estat.
        return v, estado_maximo

    def min_value_expectimax(self, currentState, depth):
        v = float('inf')
        estado_minimo = None
        self.newBoardSim(currentState)
        # Comprovem si es un estat terminal
        if depth <= 0 or self.isCheckmate():
            return self.heuristica(currentState, True)
        self.newBoardSim(currentState)
        values = []
        # En cas que no ho sigui mirem si toca moure a les blanques o a les negres
        if self.turn:
            # Busquem els posibles nous estats de les figures negres
            estadosB = self.getListNextStatesB(self.getBlackState(currentState))

            for estatsB in estadosB:
                # Creem el estat sencer per cada posible estat de Negres
                estado = self.calcularEstatSeguent(currentState, estatsB)
                if self.isWatchedBk(estado):
                    # Si posem el rei en perill no contemplem l'estat
                    continue
                if estado_minimo == None:
                    # Guardem un estat per no retornar estat = None
                    estado_minimo = estado
                self.newBoardSim(currentState)
                # Crida a la funció min_value_expectimax
                val, newState = self.max_value_expectimax(estado, depth - 1)
                self.newBoardSim(currentState)
                #Afegim els nous valors
                values.append(val)
        #Torn del blanc al min
        else:
            # Busquem els posibles nous estats de les figures blanques
            estadosW = self.getListNextStatesW(self.getWhiteState(currentState))
            for estatsW in estadosW:
                # Creem el estat sencer per cada posible estat de Blanques
                estado = self.calcularEstatSeguent(currentState, estatsW)

                if self.isWatchedWk(estado):
                    # Si posem el rei en perill no contemplem l'estat
                    continue
                if estado_minimo == None:
                    # Guardem un estat per no retornar estat = None
                    estado_minimo = estado
                self.newBoardSim(currentState)
                # Crida a la funció min_value
                val, newState = self.max_value(estado, depth - 1)
                self.newBoardSim(currentState)
                #Afegim els nous valors
                values.append(val)
        #Retornem els valors ponderats
        return self.calculateValue(values)

    def exercici5(self, depthWhite, depthBlack):
        self.turn = True
        currentState = self.getCurrentState()
        iteraciones = 0
        alpha = float('-inf')
        beta =  float('inf')
        while (not self.isCheckmateReal() and (iteraciones < 50)):
            if self.turn:
                v, nuevoEstado = self.max_value_expectimax(currentState, depthWhite)
                nuevoEstadoDefinitivo = self.calcularEstatSeguent(currentState, self.getWhiteState(nuevoEstado))
            else:
                v, nuevoEstado = self.max_value_poda(currentState, depthBlack, alpha, beta)
                nuevoEstadoDefinitivo = self.calcularEstatSeguent(currentState, self.getBlackState(nuevoEstado))

            move = self.getMovement(currentState, nuevoEstadoDefinitivo)

            self.chess.move(move[0], move[1])
            self.chess.board.print_board()
            currentState = self.getCurrentState()
            print("current State", currentState)
            print("LA V: ", v)
            self.turn = not self.turn
            iteraciones += 1
            print("ITERACION NUMERO: ", iteraciones)

        return
    def mitjana(self, values):
        sum = 0
        N = len(values)
        for i in range(N):
            sum += values[i]

        return sum / N

    def desviacio(self, values, mitjana):
        sum = 0
        N = len(values)

        for i in range(N):
            sum += pow(values[i] - mitjana, 2)

        return pow(sum / N, 1 / 2)

    def calculateValue(self, values):

        if len(values) == 0:
            return 0
        mitjana = self.mitjana(values)
        desviacio = self.desviacio(values, mitjana)
        # If deviation is 0, we cannot standardize values, since they are all equal, thus probability willbe equiprobable
        if desviacio == 0:
            # We return another value
            return values[0]

        esperanca = 0
        sum = 0
        N = len(values)
        for i in range(N):
            # Normalize value, with mean and deviation - zcore
            normalizedValues = (values[i] - mitjana) / desviacio
            # make the values positive with function e^(-x), in which x is the standardized value
            positiveValue = pow(1 / math.e, normalizedValues)
            # Here we calculate the expected value, which in the end will be expected value/sum
            # Our positiveValue/sum represent the probabilities for each value
            # The larger this value, the more likely
            esperanca += positiveValue * values[i]
            sum += positiveValue

        return esperanca / sum


if __name__ == "__main__":
    #   if len(sys.argv) < 2:
    #       sys.exit(usage())

    # intiialize board
    TA = np.zeros((8, 8))

    # Configuració inicial del taulell
    TA[7][0] = 2
    TA[7][4] = 6
    TA[0][7] = 8
    TA[0][4] = 12

    # initialise board
    print("stating AI chess... ")
    aichess = Aichess(TA, True)

    print("printing board")
    aichess.chess.boardSim.print_board()

    # Run exercise 1
    aichess.minimaxGame(3,3)
    #aichess.alphaBetaPoda(4,4)
    #aichess.exercici3(4,4)
    #aichess.expectimax(3,3)
    #aichess.exercici5(3,3)

# Add code to save results and continue with other exercises



