const miapp = Vue.createApp({
    data(){
        return{
            filtrosActivosMarcas: [],
            filtrosActivosProcesadores: [],
            busqueda: "",
            precioMax: 0,
            ocultarFiltros: true,
            productos: [{
                nombre: "Lenovo IdeaPad 3 Gen 6",
                imagen: "./resource/portatil.jpg",
                propiedades:{
                    Precio: "599,00€",
                    Procesador: "Core i5-1135G7",
                    Tamaño: "8GB RAM | 512GB SSD",
                    Pantalla: "15.6 Pulgadas"
                },
                precio: 599,
                hilos: [{
                    nombre: "Juan",
                    imagen: "./resource/usario.png",
                    titulo: "Buen producto!",
                    texto: "El Lenovo IdeaPad 3 Gen 6 es una excelente opción para aquellos que buscan una computadora portátil económica pero capaz de realizar múltiples tareas. Con una pantalla nítida y vibrante, una batería de larga duración y un procesador potente, este portátil superó mis expectativas. Además, su diseño elegante y delgado lo hace fácil de transportar. Lo recomendaría a cualquiera que busque una opción de presupuesto para tareas diarias de computación, como navegación web, edición de documentos y reproducción de medios.",
                    estrellas: 120,
                    comentarios: 46
                }],
                hilo_ref: 'Fils.html'
            },{
                nombre: "ASUS ROG Strix G18",
                imagen: "./resource/portatil2.jpg",
                propiedades:{
                    Precio: "2599,00€",
                    Procesador: "Core i9-13980HX",
                    Grafica: "NVIDIA GeForce RTX 4070",
                    Tamaño: "32 GB RAM DDR5| 1TB SSD",
                    Pantalla: "18 Pulgadas"
                },
                precio: 2599,
                hilos: [{
                    nombre: "Luismi",
                    imagen: "./resource/usario.png",
                    titulo: "Calidad-precio",
                    texto: "Caro pero es lo que hay si quieres lo último y 18\" con este portátil tendrás para unos cuantos años si te lo puedes permitir no lo dudes",
                    estrellas: 17,
                    comentarios: 6
                }],
                hilo_ref: "Fils2.html"
            }
            ],
            filtros: [{
                titulo: "Marcas",
                filtros: [
                    {nombre: "Asus", tag: "asus"},
                    {nombre: "MSI", tag: "msi"},
                    {nombre: "Lenovo", tag: "lenovo"},
                    {nombre: "Apple", tag: "apple"},
                    {nombre: "HP", tag: "hp"},
                    {nombre: "Acer", tag: "acer"},
                ]
            },{
                titulo: "Procesador",
                filtros: [
                    {nombre: "Intel Core I9",
                    tag: "i9"},
                    {nombre: "Intel Core I7",
                    tag: "i7"},
                    {nombre: "Intel Core I5",
                    tag: "i5"},
                    {nombre: "AMD Ryzen 7",
                    tag: "ryzen 7"},
                    {nombre: "AMD Ryzen 5",
                    tag: "ryzen 5"},
                ]
            }
            ]
        }
    },computed: {
        getPrecioMax() {
            return this.precioMax;
        }
    },
    methods:{
        ocultar(){
          this.ocultarFiltros = !this.ocultarFiltros;
          if(!this.ocultarFiltros){
              this.filtrosActivosProcesadores = [];
              this.filtrosActivosMarcas = [];
          }
        },
        filtroMarca(producto){
            bool_m = false;
            bool_p = false
            if(this.filtrosActivosMarcas.length === 0){
                bool_m = true;
            }
            else{
                this.filtrosActivosMarcas.forEach(item =>{
                    if (producto.nombre.toLowerCase().includes(item.toLowerCase())){
                        bool_m = true;
                    }
                });
            }
            if(this.filtrosActivosProcesadores.length === 0){
                bool_p = true;
            }
            else{
                this.filtrosActivosProcesadores.forEach(item =>{
                    if (producto.propiedades["Procesador"].toLowerCase().includes(item.toLowerCase())){
                        bool_p = true;
                    }
                });
            }
            return bool_p && bool_m
        },
        addFiltro(tag){
          this.filtros.forEach(items =>{
             items.filtros.forEach( items2 => {
                 if(items2.tag.toLowerCase().includes(tag.toLowerCase())){
                     if(items.titulo == "Marcas"){
                         this.filtrosActivosMarcas.push(tag);
                     }else if(items.titulo == "Procesador"){
                         this.filtrosActivosProcesadores.push(tag);
                     }
                 }
             });
          });
        },
        redireccionar(href){
            console.log(href)
            window.location.href = href;
        },
        deleteFiltro(tag){
            this.filtros.forEach(items =>{
                items.filtros.forEach( items2 => {
                    if(items2.tag.toLowerCase() == tag){
                        if(items.titulo == "Marcas"){
                            const index = this.filtrosActivosMarcas.indexOf(tag);
                            this.filtrosActivosMarcas.splice(index,1);
                        }else if(items.titulo == "Procesador"){
                            index = this.filtrosActivosProcesadores.indexOf(tag)
                            this.filtrosActivosProcesadores.splice(index,1)
                        }
                    }
                });
            });
        },
        setPrecioMax(num){
            this.precioMax = num;
        },
        isEmptyBusqueda(){
            return this.busqueda != "";
        },
        setBusqueda(text){
            this.busqueda = text.slice();
        },
        buscar(){
            input = document.getElementById("buscador");
            value = input.value;
            this.setBusqueda(value);
        },

        buscarGeekNote() {
            const input = document.getElementById('buscador').value.toLowerCase();
            const cards = document.getElementsByClassName('card');
          
            for (let i = 0; i < cards.length; i++) {
              const title = cards[i].querySelector('.card-title').textContent.toLowerCase();
              const cardVisible = title.includes(input);
              cards[i].style.display = cardVisible ? 'block' : 'none';
            }
          }
          
          
    }
});
miapp.component('filtros', {
    template: `
    <div>
      <h4 class="margen">{{titulo}}</h4>  
      <div class="filtros" style="display: inline-block;">
        <div class="filtros" style="margin-left: 10px; margin-top: 10px" v-for="(filtro) in filtros" :key="filtro.nombre">
          <div class="form-check">
            <input class="form-check-input" type="checkbox" :id="filtro.nombre" :value="filtro.tag" v-model="filtroSeleccionados" @change="checkboxChanged">
            <label class="form-check-label" :for="filtro.nombre">{{filtro.nombre}}</label>
          </div>
        </div>
      </div>
    </div>
  `,
    props: {
        titulo: String,
        filtros: Array,
    },
    data() {
        return {
            filtroSeleccionados: []
        }
    },
    methods: {
        checkboxChanged(event) {
            if (event.target.checked) {
                this.$parent.addFiltro(event.target.value)
            } else {
                this.$parent.deleteFiltro(event.target.value)
            }
        }
    },
});
miapp.component('producto',{
    template: `
    <img :src="producto.imagen" width="200" alt="Foto del producto"> 
    <h2 class="letra-descripción">{{ producto.nombre }}</h2>
    <ul>
      <li v-for="(value, key) in producto.propiedades" :key="key" class="letra-descripción">{{ key }}: {{ value }}</li>
    </ul>
    `,
    props:[
        "producto"
    ],
    data() {
        return {

        }
    },
})
miapp.component('barra-deslizante',{
    template: `
    <div class="filtros">
        <div class="row">
            <div class="col-12 col-container">
                <h4>{{titulo}}</h4>
            </div>
            <div class="col-12 col-container">
                <input type="range" id="price" name="price" :min="min" :max="max" :step="step" @change="onchangeRange()">
            </div>
            <div class="col-12 col-container align-items-center">
                <input type="text" id="priceInput" name="priceInput" style="border: none; background-color: transparent; text-align: center; width: 50%;" @change="onchangeText(value)">
            </div>
        </div>
    </div>
    `,
    props: {
        titulo: {type:String},
        min: {type:String},
        max: {type:String},
        step: {type:String}
    },
    data() {
        return {
        }
    },
    mounted() {
        this.setValue("3000");
    },
    computed: {

    },
    methods: {
        setValue(number){
            priceInput = document.getElementById("price")
            priceInput.value = number;
            priceText = document.getElementById("priceInput");
            priceText.value = number+"€";
            this.$parent.setPrecioMax(number)
        },
        onchangeRange() {
            priceInput = document.getElementById("price")
            currentPrice = parseInt(priceInput.value)
            priceText = document.getElementById("priceInput");
            priceText.value = currentPrice+"€";
            this.$parent.setPrecioMax(currentPrice)
        },
        onchangeText(){
            priceText = document.getElementById("priceInput");
            currentPrice = parseInt(priceText.value);
            priceInput = document.getElementById("price");
            priceInput.value = currentPrice;
            this.$parent.setPrecioMax(currentPrice)
        }
    }
})
miapp.component('hilo', {
    template: `
    <div class="row">
      <div class="col-2 col-container align-items-center">
        <img :src="hilo.imagen" class="img" width="50" alt="Foto del logo">
        <h6>{{hilo.nombre}}</h6>
      </div>
      <div class="col-10 col-container">
        <div style="font-size: larger; font-weight: bold">{{hilo.titulo}}</div>
        <div class="letra-texto">{{hilo.texto}}</div>
        <div class="interacciones" style="display: flex; justify-content: center; align-items: center;">
          <span style="margin-right: 10px;"><i class="material-icons" style="color: gold">star</i> {{hilo.estrellas}}</span>
          <span style="margin-left: 10px;"><i class="material-icons">forum</i> {{hilo.comentarios}}</span>
        </div>
      </div>
    </div>
  `,
    props:[
      "hilo"
    ],
    data() {
        return {}
    },
    methods: {}
})
function cambiarFotoPerfil() {
    var input = document.getElementById('foto');
    var imagen = document.getElementsByClassName('userpicture')[0];
    var sImagen = document.getElementById('imagenUsuario"');
    imagen.classList.add('img-circle');
    sImagen.classList.add('img-circle');


    input.addEventListener('change', function(e) {
        var archivo = e.target.files[0];
        var reader = new FileReader();

        reader.onload = function(e) {
            imagen.src = e.target.result;
            sImagen.src = e.target.result
        };

        reader.readAsDataURL(archivo);
    });

}
function cambiarUsuario(){
    var nuevoNombre = document.getElementById("nombre").value;

    // Actualizar el elemento h6 con el nuevo nombre de usuario
    var elementoNombre = document.getElementById("nombreUsuario");
    if (nuevoNombre.trim() == '') {
        // Actualizar el nombre de usuario en la parte superior derecha
        document.querySelector('.username').textContent = nuevoNombre;

    }else{
        elementoNombre.innerText = nuevoNombre;
        }
}
function cambiarPagina(){
    window.location.href= 'index.html';
}
miapp.mount('#app');
