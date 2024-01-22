#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Sep  8 11:22:03 2022

@author: ignasi
"""
import heapq
import queue

import chess
import numpy as np
import sys
import numpy as np
from numpy import random
import matplotlib.pyplot as plt
import copy
import random

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
        self.pathToTarget = []
        self.currentStateW = self.chess.boardSim.currentStateW
        self.depthMax = 8
        self.checkMate = False
        self.MatriuQ = []
        self.tasa_aprendizaje = 0.4
        self.factor_descuento = 0.8

        # Prepare a dictionary to control the visited state and at which
        # depth they were found
        self. \
            dictVisitedStates = {}
        # Dictionary to reconstruct the BFS path
        self.dictPath = {}

    def getCurrentState(self):

        return self.myCurrentStateW

    def getListNextStatesW(self, myState):

        self.chess.boardSim.getListNextStatesW(myState)
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

    def isCheckMate(self, mystate):

        # list of possible check mate states
        listCheckMateStates = [[[0, 0, 2], [2, 4, 6], [0, 4, 12]], [[0, 1, 2], [2, 4, 6], [0, 4, 12]],
                               [[0, 2, 2], [2, 4, 6], [0, 4, 12]], [[0, 6, 2], [2, 4, 6], [0, 4, 12]],
                               [[0, 7, 2], [2, 4, 6], [0, 4, 12]]]

        # Check all state permuations and if they coincide with a list of CheckMates
        for permState in list(permutations(mystate)):
            if list(permState) in listCheckMateStates:
                return True

        return False

    def DepthFirstSearch(self, currentState, depth):

        # We visited the node, therefore we add it to the list
        # In DF, when we add a node to the list of visited, and when we have
        # visited all noes, we eliminate it from the list of visited ones
        self.listVisitedStates.append(currentState)

        # is it checkmate?
        if self.isCheckMate(currentState):
            self.pathToTarget.append(currentState)
            return True

        if depth + 1 <= self.depthMax:
            for son in self.getListNextStatesW(currentState):
                if not self.isVisited(son):
                    # in the state son, the first piece is the one just moved
                    # We check the position of currentState
                    # matched by the piece moved
                    if son[0][2] == currentState[0][2]:
                        fitxaMoguda = 0
                    else:
                        fitxaMoguda = 1

                    # we move the piece to the new position
                    self.chess.moveSim(currentState[fitxaMoguda], son[0])
                    # We call again the method with the son,
                    # increasing depth
                    if self.DepthFirstSearch(son, depth + 1):
                        # If the method returns True, this means that there has
                        # been a checkmate
                        # We ad the state to the list pathToTarget
                        self.pathToTarget.insert(0, currentState)
                        return True
                    # we reset the board to the previous state
                    self.chess.moveSim(son[0], currentState[fitxaMoguda])

        # We eliminate the node from the list of visited nodes
        # since we explored all successors
        self.listVisitedStates.remove(currentState)

    def worthExploring(self, state, depth):

        # First of all, we check that the depth is bigger than depthMax
        if depth > self.depthMax: return False
        visited = False
        # check if the state has been visited
        for perm in list(permutations(state)):
            permStr = str(perm)
            if permStr in list(self.dictVisitedStates.keys()):
                visited = True
                # If there state has been visited at a epth bigger than
                # the current one, we are interestted in visiting it again
                if depth < self.dictVisitedStates[perm]:
                    # We update the depth associated to the state
                    self.dictVisitedStates[permStr] = depth
                    return True
        # Whenever not visited, we add it to the dictionary
        # at the current depth
        if not visited:
            permStr = str(state)
            self.dictVisitedStates[permStr] = depth
            return True

    def DepthFirstSearchOptimized(self, currentState, depth):
        # is it checkmate?
        if self.isCheckMate(currentState):
            self.pathToTarget.append(currentState)
            return True

        for son in self.getListNextStatesW(currentState):
            if self.worthExploring(son, depth + 1):

                # in state 'son', the first piece is the one just moved
                # we check which position of currentstate matche
                # the piece just moved
                if son[0][2] == currentState[0][2]:
                    fitxaMoguda = 0
                else:
                    fitxaMoguda = 1

                # we move the piece to the novel position
                self.chess.moveSim(currentState[fitxaMoguda], son[0])
                # we call the method with the son again, increasing depth
                if self.DepthFirstSearchOptimized(son, depth + 1):
                    # If the method returns true, this means there was a checkmate
                    # we add the state to the list pathToTarget
                    self.pathToTarget.insert(0, currentState)
                    return True
                # we return the board to its previous state
                self.chess.moveSim(son[0], currentState[fitxaMoguda])

    def reconstructPath(self, state, depth):
        # When we found the solution, we obtain the path followed to get to this
        for i in range(depth):
            self.pathToTarget.insert(0, state)
            # Per cada node, mirem quin és el seu pare
            state = self.dictPath[str(state)][0]

        self.pathToTarget.insert(0, state)

    def canviarEstat(self, start, to):
        # We check which piece has been moved from one state to the next
        if start[0] == to[0]:
            fitxaMogudaStart = 1
            fitxaMogudaTo = 1
        elif start[0] == to[1]:
            fitxaMogudaStart = 1
            fitxaMogudaTo = 0
        elif start[1] == to[0]:
            fitxaMogudaStart = 0
            fitxaMogudaTo = 1
        else:
            fitxaMogudaStart = 0
            fitxaMogudaTo = 0
        # move the piece changed
        self.chess.moveSim(start[fitxaMogudaStart], to[fitxaMogudaTo])

    def movePieces(self, start, depthStart, to, depthTo):

        # To move from one state to the next for BFS we will need to find
        # the state in common, and then move until the node 'to'
        moveList = []
        # We want that the depths are equal to find a common ancestor
        nodeTo = to
        nodeStart = start
        # if the depth of the node To is larger than that of start,
        # we pick the ancesters of the node until being at the same
        # depth
        while (depthTo > depthStart):
            moveList.insert(0, to)
            nodeTo = self.dictPath[str(nodeTo)][0]
            depthTo -= 1
        # Analogous to the previous case, but we trace back the ancestors
        # until the node 'start'
        while (depthStart > depthTo):
            ancestreStart = self.dictPath[str(nodeStart)][0]
            # We move the piece the the parerent state of nodeStart
            self.canviarEstat(nodeStart, ancestreStart)
            nodeStart = ancestreStart
            depthStart -= 1

        moveList.insert(0, nodeTo)
        # We seek for common node
        while nodeStart != nodeTo:
            ancestreStart = self.dictPath[str(nodeStart)][0]
            # Move the piece the the parerent state of nodeStart
            self.canviarEstat(nodeStart, ancestreStart)
            # pick the parent of nodeTo
            nodeTo = self.dictPath[str(nodeTo)][0]
            # store in the list
            moveList.insert(0, nodeTo)
            nodeStart = ancestreStart
        # Move the pieces from the node in common
        # until the node 'to'
        for i in range(len(moveList)):
            if i < len(moveList) - 1:
                self.canviarEstat(moveList[i], moveList[i + 1])

    def BreadthFirstSearch(self, currentState, depth):
        """
        Check mate from currentStateW
        """
        BFSQueue = queue.Queue()
        # The node root has no parent, thus we add None, and -1, which would be the depth of the 'parent node'
        self.dictPath[str(currentState)] = (None, -1)
        depthCurrentState = 0
        BFSQueue.put(currentState)
        self.listVisitedStates.append(currentState)
        # iterate until there is no more candidate nodes
        while BFSQueue.qsize() > 0:
            # Find the optimal configuration
            node = BFSQueue.get()
            depthNode = self.dictPath[str(node)][1] + 1
            if depthNode > self.depthMax:
                break
            # If it not the root node, we move the pieces from the previous to the current state
            if depthNode > 0:
                self.movePieces(currentState, depthCurrentState, node, depthNode)

            if self.isCheckMate(node):
                # Si és checkmate, construïm el camí que hem trobat més òptim
                self.reconstructPath(node, depthNode)
                break

            for son in self.getListNextStatesW(node):
                if not self.isVisited(son):
                    self.listVisitedStates.append(son)
                    BFSQueue.put(son)
                    self.dictPath[str(son)] = (node, depthNode)
            currentState = node
            depthCurrentState = depthNode

    def h(self, state):

        if state[0][2] == 2:
            posicioRei = state[1]
            posicioTorre = state[0]
        else:
            posicioRei = state[0]
            posicioTorre = state[1]
        # With the king we wish to reach configuration (2,4), calculate Manhattan distance
        fila = abs(posicioRei[0] - 2)
        columna = abs(posicioRei[1] - 4)
        # Pick the minimum for the row and column, this is when the king has to move in diagonal
        # We calculate the difference between row an colum, to calculate the remaining movements
        # which it shoudl go going straight
        hRei = min(fila, columna) + abs(fila - columna)
        # with the tower we have 3 different cases
        if posicioTorre[0] == 0 and (posicioTorre[1] < 3 or posicioTorre[1] > 5):
            hTorre = 0
        elif posicioTorre[0] != 0 and posicioTorre[1] >= 3 and posicioTorre[1] <= 5:
            hTorre = 2
        else:
            hTorre = 1
        # In our case, the heuristics is the real cost of movements
        return hRei + hTorre

    def AStarSearch(self, currentState):
        # Primer de tot ordenem tots els estats ja que a vegades s'emmagatzemen amb la torre primer i a vegades amb el rei primer
        currentState = sorted(currentState)
        frontera = []
        # Afegim l'heuristica i el estat a frontera
        frontera.append((self.h(currentState), currentState))
        self.dictPath[str(currentState)] = (
        None, -1)  # Fiquem la primera dada al diccionari dictPath, com no te pare posem None i -1
        currentStateSim = currentState

        while frontera:
            # Iniciem el bucle dels estats que tenim per recorrer i busquem l'estat amb l'heuristica minima
            minim = min(frontera, key=lambda x: x[0])
            estado_nodo = minim[1]
            # Ordenem els estats ja que a vegades s'emmagatzemen amb la torre primer i a vegades amb el rei primer
            estado_nodo = sorted(estado_nodo)
            # Afegim l'estat a la llista d'estats visitats i l'eliminem de frontera
            self.listVisitedStates.append(estado_nodo)
            frontera.remove(minim)
            # Actualitzem el taulell amb els nou moviment i actualitzem l'estat actual
            self.movePieces(currentStateSim, (self.dictPath[str(currentStateSim)][1] + 1), estado_nodo,
                            (self.dictPath[str(estado_nodo)][1] + 1))
            currentStateSim = estado_nodo

            # Comprovem si es "CheckMate"c en aquest cas reconstruim el camí i acabem el programa
            if self.isCheckMate(estado_nodo):
                self.reconstructPath(currentStateSim, self.dictPath[str(currentStateSim)][1])
                break
            # Busquem els estats vehins als quals podem accedir movent una peça
            for vecino in self.getListNextStatesW(estado_nodo):
                # Ordenem els estats ja que a vegades s'emmagatzemen amb la torre primer i a vegades amb el rei primer
                vecino = sorted(vecino)
                # Busquem si no ha estat visitat o si si que l'hem visitat però hem trobat un camí més ràpid i afegim els valors nous a frontera i a dictPath
                if (not self.isVisited(vecino)) or (
                        (self.dictPath[str(estado_nodo)][1] + 1) < self.dictPath[str(vecino)][1]):
                    self.dictPath[str(vecino)] = (estado_nodo, (self.dictPath[str(estado_nodo)][1] + 1))
                    frontera.append((self.h(vecino), vecino))

        # En cas de no trobar camí retornem fals
        return False

    def newBoardSim(self, listStates):
        # We create a  new boardSim
        TA = np.zeros((8, 8))
        for state in listStates:
            TA[state[0]][state[1]] = state[2]

        self.chess.newBoardSim(TA)

    def calcularEstatSeguent(self, currentState, estats):
        # Metode per trobar el seguent estat a partir del currentState i el nou estat de les peces d'un color només
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

    def accio_es_legal(self, fila, accio):
        torre = fila // 64
        filaTorre = torre // 8
        columnaTorre = torre % 8
        rei = fila % 64
        filaRei = rei // 8
        columnaRei = rei % 8
        if (filaTorre == filaRei) and (columnaRei == columnaTorre):
            return False
        if accio > 7:
            if (accio <= 14) or ((accio >= 22) and (accio <= 28)):
                if (filaTorre == 0) and (accio in [28, 27, 26, 25, 24, 23, 22]):
                    return True
                if (filaTorre == 1) and (accio in [27, 26, 25, 24, 23, 22, 8]):
                    return True
                if (filaTorre == 2) and (accio in [26, 25, 24, 23, 22, 8, 9]):
                    return True
                if (filaTorre == 3) and (accio in [25, 24, 23, 22, 8, 9, 10]):
                    return True
                if (filaTorre == 4) and (accio in [24, 23, 22, 8, 9, 10, 11]):
                    return True
                if (filaTorre == 5) and (accio in [23, 22, 8, 9, 10, 11, 12]):
                    return True
                if (filaTorre == 6) and (accio in [22, 8, 9, 10, 11, 12, 13]):
                    return True
                if (filaTorre == 7) and (accio in [8, 9, 10, 11, 12, 13, 14]):
                    return True
            else:
                if (columnaTorre == 0) and (accio in [15, 16, 17, 18, 19, 20, 21]):
                    return True
                if (columnaTorre == 1) and (accio in [29, 15, 16, 17, 18, 19, 20]):
                    return True
                if (columnaTorre == 2) and (accio in [30, 29, 15, 16, 17, 18, 19]):
                    return True
                if (columnaTorre == 3) and (accio in [31, 30, 29, 15, 16, 17, 18]):
                    return True
                if (columnaTorre == 4) and (accio in [32, 31, 30, 29, 15, 16, 17]):
                    return True
                if (columnaTorre == 5) and (accio in [33, 32, 31, 30, 29, 15, 16]):
                    return True
                if (columnaTorre == 6) and (accio in [34, 33, 32, 31, 30, 29, 15]):
                    return True
                if (columnaTorre == 7) and (accio in [35, 34, 33, 32, 31, 30, 29]):
                    return True
        else:
            if accio == 0 and filaRei > 0:
                return True
            elif accio == 1 and filaRei > 0 and columnaRei < 7:
                return True
            elif accio == 2 and columnaRei < 7:
                return True
            elif accio == 3 and filaRei < 7 and columnaRei < 7:
                return True
            elif accio == 4 and filaRei < 7:
                return True
            elif accio == 5 and filaRei < 7 and columnaRei > 0:
                return True
            elif accio == 6 and columnaRei > 0:
                return True
            elif accio == 7 and filaRei > 0 and columnaRei > 0:
                return True
        return False

    def crearMatriuQ(self):
        for fila in range(64 * 64):
            accions_values = []
            coincideix = 0
            if fila == 0:
                for accions in range(36):
                    accions_values.append(float('-inf'))
            else:
                coincideix = coincideix + 65
                if fila % coincideix != 0:
                    for accions in range(36):
                        if self.accio_es_legal(fila, accions):
                            accions_values.append(0)
                        else:
                            accions_values.append(float('-inf'))

                else:
                    for accions in range(36):
                        accions_values.append(float('-inf'))

            self.MatriuQ.append(accions_values)

    def getPieceState(self, state, piece):
        pieceState = None
        for i in state:
            if i[2] == piece:
                pieceState = i
                break
        return pieceState

    def mediaQtable(self):
        num_valores = 0
        suma = 0
        for i in self.MatriuQ:
            for j in i:
                if j != float('-inf'):
                    num_valores += 1
                    suma += j
        return suma / num_valores

    def accion_aleatoria2(self, estado):
        nextstates = self.getListNextStatesW(estado)
        random_action = random.choice(nextstates)
        return (random_action)

    def accion_aleatoria(self, fila_actual):
        accio = np.random.randint(0, 36)
        while self.MatriuQ[fila_actual][accio] == float('-inf'):
            accio = np.random.randint(0, 36)

        return accio

    def tomar_accion(self, estado_actual, accio):
        if accio == 0:
            nuevo_estado = estado_actual - 8
        elif accio == 1:
            nuevo_estado = estado_actual - 7
        elif accio == 2:
            nuevo_estado = estado_actual + 1
        elif accio == 3:
            nuevo_estado = estado_actual + 9
        elif accio == 4:
            nuevo_estado = estado_actual + 8
        elif accio == 5:
            nuevo_estado = estado_actual + 7
        elif accio == 6:
            nuevo_estado = estado_actual - 1
        elif accio == 7:
            nuevo_estado = estado_actual - 9
        # arriba
        elif accio == 8:
            nuevo_estado = estado_actual - 8 * 64
        elif accio == 9:
            nuevo_estado = estado_actual - 16 * 64
        elif accio == 10:
            nuevo_estado = estado_actual - 24 * 64
        elif accio == 11:
            nuevo_estado = estado_actual - 32 * 64
        elif accio == 12:
            nuevo_estado = estado_actual - 40 * 64
        elif accio == 13:
            nuevo_estado = estado_actual - 48 * 64
        elif accio == 14:
            nuevo_estado = estado_actual - 56 * 64
        # lateral derecho
        elif accio == 15:
            nuevo_estado = estado_actual + 1 * 64
        elif accio == 16:
            nuevo_estado = estado_actual + 2 * 64
        elif accio == 17:
            nuevo_estado = estado_actual + 3 * 64
        elif accio == 18:
            nuevo_estado = estado_actual + 4 * 64
        elif accio == 19:
            nuevo_estado = estado_actual + 5 * 64
        elif accio == 20:
            nuevo_estado = estado_actual + 6 * 64
        elif accio == 21:
            nuevo_estado = estado_actual + 7 * 64
        # abajo
        elif accio == 22:
            nuevo_estado = estado_actual + 8 * 64
        elif accio == 23:
            nuevo_estado = estado_actual + 16 * 64
        elif accio == 24:
            nuevo_estado = estado_actual + 24 * 64
        elif accio == 25:
            nuevo_estado = estado_actual + 32 * 64
        elif accio == 26:
            nuevo_estado = estado_actual + 40 * 64
        elif accio == 27:
            nuevo_estado = estado_actual + 48 * 64
        elif accio == 28:
            nuevo_estado = estado_actual + 56 * 64
        # lateral izquierdo
        elif accio == 29:
            nuevo_estado = estado_actual - 1 * 64
        elif accio == 30:
            nuevo_estado = estado_actual - 2 * 64
        elif accio == 31:
            nuevo_estado = estado_actual - 3 * 64
        elif accio == 32:
            nuevo_estado = estado_actual - 4 * 64
        elif accio == 33:
            nuevo_estado = estado_actual - 5 * 64
        elif accio == 34:
            nuevo_estado = estado_actual - 6 * 64
        elif accio == 35:
            nuevo_estado = estado_actual - 7 * 64
        return nuevo_estado

    def getState(self, estadoNumero):
        state = []
        state.append([0, 4, 12])
        torre = estadoNumero // 64
        filaTorre = torre // 8
        columnaTorre = torre % 8
        estadoTorre = [filaTorre, columnaTorre, 2]
        state.append(estadoTorre)
        rei = estadoNumero % 64
        filaRei = rei // 8
        columnaRei = rei % 8
        estadoRei = [filaRei, columnaRei, 6]
        state.append(estadoRei)
        return state

    def imprimir_politica_rei(self, politica_rei):
        contador = 0
        board_politica_rei = [[-1] * 8 for _ in range(8)]
        accionsRei = []
        for i in range(8):
            for j in range(8):
                posicio = i * 8 + j
                for h in range(64):
                    accio = politica_rei[h * 64 + posicio]
                    accionsRei.append(accio)
                contadorAccions = np.bincount(accionsRei)
                accionsFrequents = np.argmax(contadorAccions)

                if accionsFrequents == 0:
                    board_politica_rei[i][j] = "↑"
                elif accionsFrequents == 1:
                    board_politica_rei[i][j] = "↗"
                elif accionsFrequents == 2:
                    board_politica_rei[i][j] = "→"
                elif accionsFrequents == 3:
                    board_politica_rei[i][j] = "↘"
                elif accionsFrequents == 4:
                    board_politica_rei[i][j] = "↓"
                elif accionsFrequents == 5:
                    board_politica_rei[i][j] = "↙"
                elif accionsFrequents == 6:
                    board_politica_rei[i][j] = "←"
                elif accionsFrequents == 7:
                    board_politica_rei[i][j] = "↖"
                else:
                    board_politica_rei[i][j] = "o"
            else:
                board_politica_rei[j][i] = "o"
                continue
        print("BOARD POLITICA REI")
        for i in range(len(board_politica_rei)):
            print(board_politica_rei[i])

    def imprimir_politica_torre(self, politica_torre):
        contador = 0
        board_politica_torre = [[-1] * 8 for _ in range(8)]
        accionsTorre = []
        for i in range(8):
            for j in range(8):
                posicio = i * 8 + j
                for h in range(64):
                    accio = politica_torre[posicio * 64 + h]
                    accionsTorre.append(accio)
                contadorAccions = np.bincount(accionsTorre)
                accionsFrequents = np.argmax(contadorAccions)

                if accionsFrequents == 8:
                    board_politica_torre[i][j] = "↑1"
                elif accionsFrequents == 9:
                    board_politica_torre[i][j] = "↑2"
                elif accionsFrequents == 10:
                    board_politica_torre[i][j] = "↑3"
                elif accionsFrequents == 11:
                    board_politica_torre[i][j] = "↑4"
                elif accionsFrequents == 12:
                    board_politica_torre[i][j] = "↑5"
                elif accionsFrequents == 13:
                    board_politica_torre[i][j] = "↑6"
                elif accionsFrequents == 14:
                    board_politica_torre[i][j] = "↑7"
                elif accionsFrequents == 15:
                    board_politica_torre[i][j] = "→1"
                elif accionsFrequents == 16:
                    board_politica_torre[i][j] = "→2"
                elif accionsFrequents == 17:
                    board_politica_torre[i][j] = "→3"
                elif accionsFrequents == 18:
                    board_politica_torre[i][j] = "→4"
                elif accionsFrequents == 19:
                    board_politica_torre[i][j] = "→5"
                elif accionsFrequents == 20:
                    board_politica_torre[i][j] = "→6"
                elif accionsFrequents == 21:
                    board_politica_torre[i][j] = "→7"
                elif accionsFrequents == 22:
                    board_politica_torre[i][j] = "↓1"
                elif accionsFrequents == 23:
                    board_politica_torre[i][j] = "↓2"
                elif accionsFrequents == 24:
                    board_politica_torre[i][j] = "↓3"
                elif accionsFrequents == 25:
                    board_politica_torre[i][j] = "↓4"
                elif accionsFrequents == 26:
                    board_politica_torre[i][j] = "↓5"
                elif accionsFrequents == 27:
                    board_politica_torre[i][j] = "↓6"
                elif accionsFrequents == 28:
                    board_politica_torre[i][j] = "↓7"
                elif accionsFrequents == 29:
                    board_politica_torre[i][j] = "←1"
                elif accionsFrequents == 30:
                    board_politica_torre[i][j] = "←2"
                elif accionsFrequents == 31:
                    board_politica_torre[i][j] = "←3"
                elif accionsFrequents == 32:
                    board_politica_torre[i][j] = "←4"
                elif accionsFrequents == 33:
                    board_politica_torre[i][j] = "←5"
                elif accionsFrequents == 34:
                    board_politica_torre[i][j] = "←6"
                elif accionsFrequents == 35:
                    board_politica_torre[i][j] = "←7"
                else:
                    board_politica_torre[i][j] = "o"
            else:
                board_politica_torre[j][i] = "o"
                continue
        print("BOARD POLITICA TORRE")
        for i in range(len(board_politica_torre)):
            print(board_politica_torre[i])

    def Qlearning(self, board_inicial):
        board_actual = board_inicial
        self.crearMatriuQ()
        num_iter = 0
        episodios_convergencia = 100  # Número de episodios consecutivos para confirmar convergencia
        contador_convergencia = 0
        list_qt = []
        media_anterior = 0
        delta = []
        list_qt.append(self.MatriuQ)
        numero_episodios = 4000
        epsilon = 0.5
        for cont in range(numero_episodios):
            num_iter += 1
            estado_actual = board_actual
            epsilon = epsilon * 0.99
            while not self.isCheckMate(estado_actual):
                self.newBoardSim(estado_actual)
                torre = self.getPieceState(estado_actual, 2)
                posicioTorre = torre[0] * 8 + torre[1]
                rei = self.getPieceState(estado_actual, 6)
                posicioRei = rei[0] * 8 + rei[1]
                posicioFila = posicioTorre * 64 + posicioRei

                if (random.random() < epsilon):
                    accion = self.accion_aleatoria(posicioFila)
                else:
                    accion = np.argmax(self.MatriuQ[posicioFila])

                nueva_Fila = self.tomar_accion(posicioFila, accion)
                if nueva_Fila % 65 == 0:
                    self.MatriuQ[posicioFila][accion] = float('-inf')
                    continue

                nuevo_estado = self.getState(nueva_Fila)

                if self.isCheckMate(nuevo_estado):
                    recompensa = 100
                else:
                    recompensa = -1

                self.newBoardSim(nuevo_estado)

                self.MatriuQ[posicioFila][accion] += self.tasa_aprendizaje * (
                            recompensa + self.factor_descuento * np.max(self.MatriuQ[nueva_Fila]) -
                            self.MatriuQ[posicioFila][accion])

                estado_actual = nuevo_estado

            media_Qtable = self.mediaQtable()
            print("Mitja Qtable episodi ", cont, ": ", media_Qtable)

            list_qt.append(copy.deepcopy(self.MatriuQ))

            if abs(media_Qtable - media_anterior) < 0.000001:
                contador_convergencia += 1

            else:
                contador_convergencia = 0
            delta.append(abs(media_Qtable - media_anterior))
            media_anterior = media_Qtable
            if contador_convergencia >= episodios_convergencia:
                print("Ha convergit a l'iteració: ", num_iter)
                break
        print(num_iter)

        board_inici = [[7, 0, 2], [7, 4, 6], [0, 4, 12]]
        # board_actual = board_inici
        board_actual = board_inicial
        path = [board_inici]

        while not self.isCheckMate(board_actual):
            print("estado: ", board_actual)
            torre = self.getPieceState(board_actual, 2)  # comprovat
            posicioTorre = torre[0] * 8 + torre[1]
            rei = self.getPieceState(board_actual, 6)
            posicioRei = rei[0] * 8 + rei[1]
            posicioFila = posicioTorre * 64 + posicioRei
            accio = np.argmax(self.MatriuQ[posicioFila])
            print("ACCIO: ", accio)
            nueva_Fila = self.tomar_accion(posicioFila, accio)  # comprovat
            nuevo_estado = self.getState(nueva_Fila)

            # self.newBoardSim(nuevo_estado)
            board_actual = nuevo_estado
            path.append(nuevo_estado)

        return path, delta, list_qt

    def drunkenSailor(self, board_inicial):
        board_actual = board_inicial
        self.crearMatriuQ()
        num_iter = 0
        episodios_convergencia = 40  # Número de episodios consecutivos para confirmar convergencia
        contador_convergencia = 0
        list_qt = []
        media_anterior = 0
        delta = []
        list_qt.append(self.MatriuQ)
        numero_episodios = 4000
        epsilon = 0.5
        for cont in range(numero_episodios):
            # print("SE viene QTABLE")
            # for i in self.MatriuQ:
            #    print(i)
            #    break
            num_iter += 1
            estado_actual = board_actual
            it = 0
            epsilon = epsilon * 0.99
            while not self.isCheckMate(estado_actual):
                it += 1
                self.newBoardSim(estado_actual)
                torre = self.getPieceState(estado_actual, 2)  # comprovat
                posicioTorre = torre[0] * 8 + torre[1]
                rei = self.getPieceState(estado_actual, 6)
                posicioRei = rei[0] * 8 + rei[1]
                posicioFila = posicioTorre * 64 + posicioRei

                if (random.random() < epsilon):
                    accion = self.accion_aleatoria(posicioFila)  # comprovat
                    if (random.random() < 0.01):
                        nueva_accion = accion
                        while accion == nueva_accion:
                            nueva_accion = self.accion_aleatoria(posicioFila)  # comprovat
                        accion = nueva_accion
                else:
                    accion = np.argmax(self.MatriuQ[posicioFila])
                    if (random.random() < 0.01):
                        nueva_accion = accion
                        while accion == nueva_accion:
                            nueva_accion = self.accion_aleatoria(posicioFila)  # comprovat
                        accion = nueva_accion

                nueva_Fila = self.tomar_accion(posicioFila, accion)  # comprovat
                if nueva_Fila % 65 == 0:
                    self.MatriuQ[posicioFila][accion] = float('-inf')
                    continue

                nuevo_estado = self.getState(nueva_Fila)
                if self.isCheckMate(nuevo_estado):
                    recompensa = 100
                else:
                    recompensa = -1
                self.newBoardSim(nuevo_estado)

                self.MatriuQ[posicioFila][accion] += self.tasa_aprendizaje * (
                            recompensa + self.factor_descuento * np.max(self.MatriuQ[nueva_Fila]) -
                            self.MatriuQ[posicioFila][accion])

                estado_actual = nuevo_estado

            media_Qtable = self.mediaQtable()
            print("Mitja Qtable episodi ", cont, ": ", media_Qtable)
            # print(self.MatriuQ[posicioTorre][posicioRei])
            list_qt.append(copy.deepcopy(self.MatriuQ))

            if abs(media_Qtable - media_anterior) < 0.000001:
                contador_convergencia += 1

            else:
                contador_convergencia = 0
            delta.append(abs(media_Qtable - media_anterior))
            media_anterior = media_Qtable
            if contador_convergencia >= episodios_convergencia:
                print("Ha convergit a l'iteració: ", num_iter)
                break
        print(num_iter)

        board_inici = [[7, 0, 2], [7, 4, 6], [0, 4, 12]]
        # board_actual = board_inici
        board_actual = board_inicial
        path = [board_inici]

        while not self.isCheckMate(board_actual):
            print("estado: ", board_actual)
            torre = self.getPieceState(board_actual, 2)  # comprovat
            posicioTorre = torre[0] * 8 + torre[1]
            rei = self.getPieceState(board_actual, 6)
            posicioRei = rei[0] * 8 + rei[1]
            posicioFila = posicioTorre * 64 + posicioRei
            accio = np.argmax(self.MatriuQ[posicioFila])
            print("ACCIO: ", accio)
            nueva_Fila = self.tomar_accion(posicioFila, accio)  # comprovat
            nuevo_estado = self.getState(nueva_Fila)

            # self.newBoardSim(nuevo_estado)
            board_actual = nuevo_estado
            path.append(nuevo_estado)

        return path, delta, list_qt


def translate(s):
    """
    Translates traditional board coordinates of chess into list indices
    """

    try:
        row = int(s[0])
        col = s[1]
        if row < 1 or row > 8:
            print(s[0] + "is not in the range from 1 - 8")
            return None
        if col < 'a' or col > 'h':
            print(s[1] + "is not in the range from a - h")
            return None
        dict = {'a': 0, 'b': 1, 'c': 2, 'd': 3, 'e': 4, 'f': 5, 'g': 6, 'h': 7}
        return (8 - row, dict[col])
    except:
        print(s + "is not in the format '[number][letter]'")
        return None


if __name__ == "__main__":

    if len(sys.argv) < 1:
        sys.exit(1)

    # intiialize board
    TA = np.zeros((8, 8))
    # load initial state
    # white pieces
    TA[7][0] = 2
    TA[7][4] = 6
    TA[0][4] = 12

    # initialise bord
    print("stating AI chess... ")
    aichess = Aichess(TA, True)
    currentState = aichess.chess.board.currentStateW.copy()
    print("printing board")
    aichess.chess.boardSim.print_board()

    # get list of next states for current state
    print("current State", currentState, "\n")

    print("QLEARN")
    path, delta, list_qt = aichess.Qlearning(currentState)

    # print("DRUNKEN SAILOR")
    # path, delta, list_qt = aichess.drunkenSailor(currentState)

    print("path")
    for i in range(1, len(path)):
        print("estado actual: ", path[i - 1], "nuevo estado: ", path[i])

    mida_llista = len(list_qt)

    print("\nPRIMERA Q-TABLE\n")
    for i in range(8):
        print(list_qt[1][i])

    print("\nQ-TABLE INTERMITJA\n")
    for i in range(8):
        print(list_qt[mida_llista // 3][i])

    print("\nSEGONA Q-TABLE INTERMITJA\n")
    for i in range(8):
        print(list_qt[(mida_llista // 3) * 2][i])

    print("\nºQ-TABLE FINAL\n")
    for i in range(8):
        print(list_qt[mida_llista - 1][i])

    '''
    aichess.AStarSearch(currentState)
    print("#A* move sequence: ", aichess.pathToTarget)
    print("A* End\n")

    print("A* printing end state")
    aichess.chess.boardSim.print_board()
    '''


