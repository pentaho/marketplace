define( ["purchase", "jquery"], function( purchase, $ ) {
  $( "body" ).append( "HELLO FROM JQUERY!!!!!!" );
  var result = purchase.purchaseProduct();
  var resultDiv = document.getElementById("result");
  resultDiv.innerHTML = result;
  return result;
});