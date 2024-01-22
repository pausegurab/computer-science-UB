$('.capa-data').hide();
$('.container-data').hide();
$('#loadPhoto').hide();

$('.container-data').css({
    
    bottom: '-450px'
    
});


$('.request').on('click', function(){
    
        $('.capa-data').fadeIn();

        $('.container-data').show();

        $('.container-data').animate({

            bottom: '0px'

        });
})

$('.capa-data').on('click', function(){
    
    $(this).fadeOut();
    
     $('.container-data').animate({

            bottom: '-450px'

        });
    
    
});


$('.perfil-photo').on('click', function(){
    $('#loadPhoto').click();
})



$(function() {
  $('#loadPhoto').change(function(e) {
      addImage(e); 
     });

     function addImage(e){
      var file = e.target.files[0],
      imageType = /image.*/;
    
      if (!file.type.match(imageType))
       return;
  
      var reader = new FileReader();
      reader.onload = fileOnload;
      reader.readAsDataURL(file);
     }
  
     function fileOnload(e) {
      var result=e.target.result;
      $('#photoSelect').attr("src",result);
     }
    });

    $(document).ready(function() {

      $('#btn-comment').on('click', function() {
        var mensaje = $('#mensaje').val(); // obtener el texto de mensaje en fils.html
    
        if (mensaje) { // verificar si el campo de texto no está vacío
          var nuevoMensaje = $('<div class="mensaje">' + mensaje + '</div>'); // crear un nuevo elemento con el mensaje
          $(this).before(nuevoMensaje); // agregar el nuevo elemento antes del botón
        }
      });
    
    });
    