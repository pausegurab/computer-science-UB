#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from typing import List
import numpy as np
from numpy import random
import matplotlib.pyplot as plt
import copy

class Exercici1P3():
    board = []
    board_recompensa = []
    estat_inicial = 1
    matriuQ = []
    estat_objectiu = 11
    tasa_aprendizaje = 0.1
    factor_descuento = 0.9
    numero_episodios = 4000
    epsilon = 0.2
    num_acciones = 3

    def crearTaulell(self, midax, miday):
        self.board = [[-1] * midax for _ in range(miday)]
        self.board[0][midax - 1] = 100
        self.board[1][1] = None
        self.board_recompensa = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 100]

    def crearTaulellImproved(self, midax ,miday):
        self.board = [[-1] * midax for _ in range(miday)]
        self.board[2][0] = -5
        self.board[1][0] = -4
        self.board[0][0] = -3
        self.board[2][2] = -3
        self.board[2][1] = -4
        self.board[1][2] = -2
        self.board[2][3] = -2
        self.board[0][1] = -2
        self.board[0][midax - 1] = 100
        self.board[1][1] = None
        self.board_recompensa = [-5, -4, -3, -4, -2, -3, -2, -1, -2, -1, 100]

    def crearMatriuQ(self):
        estat = 0
        for fila in range(len(self.board)):
            for columna in range(len(self.board[0])):
                if self.board[fila][columna] != None:
                    estat += 1
                    column_values = []
                    for movimiento in range(4):
                        if(self.es_legal(movimiento, estat)):
                            column_values.append(0)
                        else:
                            column_values.append(float('-inf')) # AMb none no funciona
                    self.matriuQ.append(column_values)
                else:
                    continue


    def accion_aleatoria(self, estado_actual):
        accio = np.random.randint(0,self.num_acciones)
        while not (self.es_legal(accio, estado_actual)):
            accio = np.random.randint(self.num_acciones)

        return accio

    def mediaQtable(self):
        num_valores = 0
        suma = 0
        for i in self.matriuQ:
            for j in i:
                if j != float('-inf'):
                    num_valores += 1
                    suma += j
        return suma / num_valores


    def es_legal(self, accio, estado_actual):
        if(estado_actual == 1):
            if accio == 0 or accio == 1:
                return True
            else:
                return False
        elif (estado_actual == 2):
            if accio == 0 or accio == 2:
                return True
            else:
                return False
        elif(estado_actual == 3):
            if accio == 1 or accio == 2:
                return True
            else:
                return False

        elif (estado_actual == 4 or estado_actual == 5):
            if accio == 1 or accio == 3:
                return True
            else:
                return False
        elif (estado_actual == 6):
            if accio == 0 or accio == 1 or accio == 3:
                return True
            else:
                return False

        elif(estado_actual == 7):
            if accio == 0 or accio == 1 or accio == 2:
                return True
            else:
                return False

        elif (estado_actual == 8):
            if accio == 1 or accio == 2 or accio == 3:
                return True
            else:
                return False
        elif (estado_actual == 9):
            if accio == 0 or accio == 3:
                return True
            else:
                return False

        elif (estado_actual == 10):
            if accio == 0 or accio == 2 or accio == 3:
                return True
            else:
                return False
        elif (estado_actual == 11):
            return True
        return False

    def get_all_possible_actions(self, estado_actual):
        if estado_actual == 1:
            return [0, 1]
        elif estado_actual == 2:
            return [0, 2]
        elif estado_actual == 3:
            return [1, 2]
        elif estado_actual == 4 or estado_actual == 5:
            return [1, 3]
        elif estado_actual == 6:
            return [0, 1, 3]
        elif estado_actual == 7:
            return [0, 1, 2]
        elif estado_actual == 8:
            return [1, 2, 3]
        elif estado_actual == 9:
            return [0, 3]
        elif estado_actual == 10:
            return [0, 2, 3]
        elif estado_actual == 11:
            return [0, 1, 2, 3] 
        return []

    def tomar_accion(self, accio, estado_actual):
        if accio == 0:
            nuevo_estado = estado_actual + 1
        elif accio == 1:
            if (estado_actual == 3 ) or (estado_actual == 4):
                nuevo_estado = estado_actual + 2
            else:
                nuevo_estado = estado_actual + 3
        elif accio == 2:
            nuevo_estado = estado_actual - 1
        elif accio == 3:
            if (estado_actual == 5) or (estado_actual == 6):
                nuevo_estado = estado_actual -2
            else:
                nuevo_estado = estado_actual - 3
        else:
            return "error"

        recompensa = self.board_recompensa[nuevo_estado-1]
        return recompensa, nuevo_estado

    def getAccio(self, estado_actual, estado_nuevo):
        if (estado_actual == 1):
            if estado_nuevo == 2:
                return 0
            if estado_nuevo == 4:
                return 1

        elif (estado_actual == 2):
            if estado_nuevo == 3:
                return 0
            if estado_nuevo == 1:
                return 2

        elif (estado_actual == 3):
            if estado_nuevo == 2:
                return 2
            if estado_nuevo == 5:
                return 1

        elif (estado_actual == 4):
            if estado_nuevo == 1:
                return 3
            if estado_nuevo == 6:
                return 1

        elif (estado_actual == 5):
            if estado_nuevo == 3:
                return 3
            if estado_nuevo == 8:
                return 1

        elif (estado_actual == 6):
            if estado_nuevo == 4:
                return 3
            if estado_nuevo == 7:
                return 0
            if estado_nuevo == 9:
                return 1

        elif (estado_actual == 7):
            if estado_nuevo == 6:
                return 2
            if estado_nuevo == 8:
                return 0
            if estado_nuevo == 10:
                return 1

        elif (estado_actual == 8):
            if estado_nuevo == 5:
                return 3
            if estado_nuevo == 7:
                return 2
            if estado_nuevo == 11:
                return 1

        elif (estado_actual == 9):
            if estado_nuevo == 6:
                return 3
            if estado_nuevo == 10:
                return 0

        elif (estado_actual == 10):
            if estado_nuevo == 7:
                return 3
            if estado_nuevo == 9:
                return 2
            if estado_nuevo == 11:
                return 0

        return None

    def Qlearning(self, board_actual):
        num_iter = 0
        episodios_convergencia = 100 # Número de episodios consecutivos para confirmar convergencia
        contador_convergencia = 0
        list_qt = []
        list_politica = []
        media_anterior = 0
        delta = []
        list_qt.append(self.matriuQ)
        for cont in range(self.numero_episodios):
            num_iter += 1
            estado_actual = board_actual
            while(estado_actual != self.estat_objectiu):
                if (random.random() < self.epsilon):
                    accion = self.accion_aleatoria(estado_actual)
                else:
                    accion = np.argmax(self.matriuQ[estado_actual - 1])
                recompensa, nuevo_estado = self.tomar_accion(accion, estado_actual)

                self.matriuQ[estado_actual-1][accion] += self.tasa_aprendizaje * (recompensa + self.factor_descuento * np.max(self.matriuQ[nuevo_estado-1]) - self.matriuQ[estado_actual-1][accion])
                estado_actual = nuevo_estado
                politica_optima = np.argmax(self.matriuQ, axis=1)

            media_Qtable = self.mediaQtable()
            list_politica.append(politica_optima)
            list_qt.append(copy.deepcopy(self.matriuQ))

            if abs(media_Qtable - media_anterior) < 0.0000000001:
                contador_convergencia += 1

            else:
                contador_convergencia = 0
            delta.append(abs(media_Qtable - media_anterior))
            media_anterior = media_Qtable
            if contador_convergencia >= episodios_convergencia:
                print("Ha convergit a l'iteració: ", num_iter)
                break
        print(num_iter)

        return politica_optima, delta, list_qt, list_politica

    def drunkenSailor(self, board_actual):
        num_iter = 0
        episodios_convergencia = 40 # Número de episodios consecutivos para confirmar convergencia
        contador_convergencia = 0
        media_anterior = 0
        list_qt = []
        list_politica = []
        delta = []
        list_qt.append(self.matriuQ)
        for cont in range(self.numero_episodios):
            num_iter += 1
            estado_actual = board_actual
            while estado_actual != self.estat_objectiu:
                if random.random() < self.epsilon:
                    accion = self.accion_aleatoria(estado_actual)
                    if random.random() <= 0.01:
                        all_possible_actions = self.get_all_possible_actions(estado_actual)
                        all_possible_actions.remove(accion)
                        if all_possible_actions:
                            accion = random.choice(all_possible_actions)

                else:
                    accion = np.argmax(self.matriuQ[estado_actual - 1])
                    if random.random() <= 0.01:
                        all_possible_actions = self.get_all_possible_actions(estado_actual)
                        all_possible_actions.remove(accion)
                        if all_possible_actions:
                            accion = random.choice(all_possible_actions)
                recompensa, nuevo_estado = self.tomar_accion(accion, estado_actual)

                self.matriuQ[estado_actual-1][accion] += self.tasa_aprendizaje * (recompensa + self.factor_descuento * np.max(self.matriuQ[nuevo_estado-1]) - self.matriuQ[estado_actual-1][accion])
                estado_actual = nuevo_estado
                politica_optima = np.argmax(self.matriuQ, axis=1)

            media_Qtable = self.mediaQtable()
            list_politica.append(politica_optima)
            list_qt.append(copy.deepcopy(self.matriuQ))

            if abs(media_Qtable - media_anterior) < 0.0000000001:
                contador_convergencia += 1

            else:
                contador_convergencia = 0
            delta.append(abs(media_Qtable - media_anterior))
            media_anterior = media_Qtable
            if contador_convergencia >= episodios_convergencia:
                print("Ha convergit a l'iteració: ", num_iter)
                break
        print(delta)
        return politica_optima, delta, list_qt, list_politica

    def imprimir_politica(self, politica, midax, miday):
        contador = 0
        board_politiques = [[-1] * midax for _ in range(miday)]
        for i in range(len(self.board[0])):
            for j in range(len(self.board) - 1, -1, -1):
                if self.board[j][i] != None:
                    politica_contador = politica[contador]
                    if politica_contador == 0:
                        board_politiques[j][i] = "↑"
                    elif politica_contador == 1:
                        board_politiques[j][i] = "→"
                    elif politica_contador == 2:
                        board_politiques[j][i] = "↓"
                    elif politica_contador == 3:
                        board_politiques[j][i] = "←"
                    else:
                        board_politiques[j][i] = "o"

                    contador += 1
                else:
                    board_politiques[j][i] = "o"
                    continue
        for i in range(len(board_politiques)):
            print(board_politiques[i])


if __name__ == "__main__":

    exercici_instance = Exercici1P3()
    exercici_instance.crearTaulell(4, 3)
    #Taulell Improved
    #exercici_instance.crearTaulellImproved(4, 3)
    exercici_instance.crearMatriuQ()
    board_pieza = 1
    #politica, delta, list_qtables, list_politicas = exercici_instance.Qlearning(board_pieza)
    #drunken Saylor
    politica, delta, list_qtables, list_politicas = exercici_instance.drunkenSailor(board_pieza)

    mida_llista = len(list_qtables)

    print("\nPRIMERA Q-TABLE I POLITICA INICIAL\n")
    for i in range(len(exercici_instance.matriuQ)):
        print(list_qtables[1][i])
    exercici_instance.imprimir_politica(list_politicas[0], 4, 3)
    print("\nQ-TABLE INTERMITJA I LA SEVA POLITICA\n")
    for i in range(len(exercici_instance.matriuQ)):
        print(list_qtables[mida_llista // 3][i])
    exercici_instance.imprimir_politica(list_politicas[(mida_llista // 3) - 1], 4, 3)

    print("\nSEGONA Q-TABLE INTERMITJA I LA SEVA POLITICA\n")
    for i in range(len(exercici_instance.matriuQ)):
        print(list_qtables[(mida_llista // 3) * 2][i])
    exercici_instance.imprimir_politica(list_politicas[(mida_llista // 3) * 2 - 1], 4, 3)

    print("\nºQ-TABLE FINAL I LA SEVA POLITICA\n")
    for i in range(len(exercici_instance.matriuQ)):
        print(list_qtables[mida_llista - 1][i])
    exercici_instance.imprimir_politica(list_politicas[mida_llista - 2], 4, 3)

    plt.figure(figsize=(24, 12))
    plt.plot(delta, linestyle = '-')
    delta_min, delta_max = min(delta), max(delta)
    ticks = np.arange(np.floor(delta_min), np.ceil(delta_max) + 0.05, 0.05)
    plt.yticks(ticks)
    plt.title('Delta Q-values')
    plt.xlabel('Episodis')
    plt.ylabel('Delta')
    plt.show()

