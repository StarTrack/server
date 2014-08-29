$(function(){
  console.log("a");
  //Ouvrir popin
  $('.info').click(function(e){
    $('.pop').fadeIn("fast", function() {
    // Animation complete.
     $('.pop').addClass('visible');
    });
  })

  //Fermer popup
  $('.cross').click(function(){
    $('.pop').removeClass('visible')
    $('.pop').fadeOut('fast');
  });
});