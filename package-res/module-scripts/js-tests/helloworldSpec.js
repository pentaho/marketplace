define(['purchase', 'jquery'], function(Purchase, $) {

	describe( "JS helloworld unit tests", function() {
	
	  it( "contains spec with an expectation", function() {
		expect( true ).toBe( true );
	  });
	  
	  it( "should reserve the produtct", function() {
		var result = Purchase.purchaseProduct();
		expect ( result ).toBe( "Reserved Product!" );
	  });
	  
	  it( "should load jquery correctly via requireJS", function() {
		expect( $ ).not.toBeNull();
	  });
	  
	});
});