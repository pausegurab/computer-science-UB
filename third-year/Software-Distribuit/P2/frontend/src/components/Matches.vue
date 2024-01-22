/* eslint-disable */
<template>
  <div id="app">
    <div v-if="is_showing_cart" class="container">
      <div class="row">
        <div class="col">
          <h1 style="text-align: start"> Sport matches </h1>
        </div>
        <div class="col">
           <button class="btn btn-outline-primary" @click="veureCistella" style="justify-content: end;">Veure cistella</button>
        </div>
      </div>
      <div class="row" style="background-color: #ffe0d6;">
        <div class="col-lg-4 col-md-6 mb-4" v-for="(match) in matches" :key="match.id">
          <br>
          <div class="card" style="width: 18rem;">
            <img class="card-img-top" :src='require("../assets/" + match.competition.sport +".jpg")'>
            <div class="card-body">
              <h5>{{ match.competition.sport }} - {{ match.competition.category }}</h5>
              <h6>{{ match.competition.name }}</h6>
              <h6><strong>{{ match.local.name }}</strong> ({{ match.local.country }}) vs <strong>{{ match.visitor.name }}</strong> ({{ match.visitor.country }})</h6>
              <h6>{{ match.date.substring(0,10) }}</h6>
              <h6>{{ match.price }} &euro;</h6>
              <h6>Tickets disponibles: {{match.total_available_tickets}}</h6>
              <button class="btn btn-success" @click="addEventToCart(match)">Afegir a la cistella</button>
          </div>
        </div>
        </div>
      </div>
    </div>
    <div v-else class="container">
      <div class="row">
        <div class="col">
          <h1 style="text-align: start"> Sport matches </h1>
        </div>
        <div class="col">
           <button class="btn btn-outline-primary" @click="veureCistella" style="justify-content: end;">Veure cistella</button>
        </div>
      </div>
      <div class="row" style="justify-content: center; margin-top: 80px;">
        <table v-if="matches_added.length > 0" class="table table-striped">

  <thead>
  <tr>
    <th>Sport</th>
    <th>Competition</th>
    <th>Match</th>
    <th>Quantity</th>
    <th>Price(&euro;)</th>
    <th>Total</th>
  </tr>
  </thead>
  <tbody>
    <tr v-for="(item,index) in matches_added" :key="item.match.id">
      <td>{{ item.match.competition.sport }}</td>
      <td>{{ item.match.competition.name }}</td>
      <td>{{ item.match.local.name }} vs {{ item.match.visitor.name }}</td>
      <td>{{matches_added[index].quantity}}<button class="btn btn-success btn-sm" style=margin:1px; @click="buyMore(index)">+</button> <button class="btn btn-danger btn-sm" @click="buyLess(index)" :disabled="item.quantity<1">-</button> </td>
      <td>{{item.match.price }}</td>
      <td>{{item.match.price * item.quantity}}</td>
      <td><button class="btn btn-danger" @click="eliminarEntrada(index)">Eliminar entrada</button></td>

    </tr>
</tbody>
</table>
        <p v-else style="align-content: center">Your cart is currently empty.
        </p>
        <div class="row" style="margin-top: 30px;">
<button class="btn btn-secondary" @click="veureCistella" style="margin: 10px;">
  Enrere
</button>
            <button class="btn btn-success" @click="finalizePurchase" style="margin: 10px;">
  Finalitza la compra
</button>
            </div>

        </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  data () {
    return {
      is_showing_cart: true,
      message: 'Tickets',
      tickets_bought: 0,
      money_available: 100,
      price_match: 10,
      avaliable_tickets: 15,
      matches: [],
      matches_added: []
    }
  },
  methods: {
    buyTicket () {
      this.tickets_bought += 1
      this.avaliable_tickets -= 1
      this.money_available -= this.price_match
    },
    returnTicket () {
      this.tickets_bought -= 1
      this.avaliable_tickets += 1
      this.money_available += this.price_match
    },
    veureCistella () {
      this.is_showing_cart = !this.is_showing_cart
    },
    buyMore (index) {
      this.matches_added.at(index).quantity++
    },
    buyLess (index) {
      this.matches_added.at(index).quantity--
    },
    addEventToCart (match) {
      this.matches_added.push({match: match, quantity: 1})
    },
    eliminarEntrada (index) {
      this.matches_added.splice(index, 1)
    },
    addPurchase (parameters) {
      const path = 'http://localhost:8000/orders/pau99'
      axios.post(path, parameters)
        .then(() => {
          console.log('Order done')
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.log(error)
          this.getMatches()
        })
    },
    finalizePurchase () {
      for (let i = 0; i < this.matches_added.length; i += 1) {
        const parameters = {
          match_id: this.matches_added[i].match.id,
          tickets_bought: this.matches_added[i].quantity

        }
        this.addPurchase(parameters)
      }
      this.matches_added = []
    },

    getMatches () {
      const pathMatches = 'http://localhost:8000/matches/'
      const pathCompetition = 'http://localhost:8000/competitions/'

      axios.get(pathMatches)
        .then((res) => {
          var matches = res.data.filter((match) => {
            return match.competition.name != null
          })
          var promises = []
          for (let i = 0; i < matches.length; i++) {
            const promise = axios.get(pathCompetition + matches[i].competition.name)
              .then((resCompetition) => {
                delete matches[i].competition_id
                matches[i].competition = {
                  'name': resCompetition.data.competition.name,
                  'category': resCompetition.data.competition.category,
                  'sport': resCompetition.data.competition.sport
                }
              })
              .catch((error) => {
                console.error(error)
              })
            promises.push(promise)
          }
          Promise.all(promises).then((_) => {
            this.matches = matches
          })
        })
        .catch((error) => {
          console.error(error)
        })
    }
  },

  created () {
    this.getMatches()
  }
}
</script>
