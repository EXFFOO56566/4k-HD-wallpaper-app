<script>

	<?php if ( $this->config->item( 'client_side_validation' ) == true ): ?>

	function jqvalidate() {

		$('#color-form').validate({
			rules:{
				name:{
					blankCheck : "",
					minlength: 3,
					remote: "<?php echo $module_site_url .'/ajx_exists/'.@$col->id; ?>"
				}
			},
			messages:{
				name:{
					blankCheck : "<?php echo get_msg( 'err_color_name' ) ;?>",
					minlength: "<?php echo get_msg( 'err_color_len' ) ;?>",
					remote: "<?php echo get_msg( 'err_color_exist' ) ;?>."
				}
			}
		});
		// custom validation
		jQuery.validator.addMethod("blankCheck",function( value, element ) {
			
			   if(value == "") {
			    	return false;
			   } else {
			    	return true;
			   }
		})
	}

	<?php endif; ?>

	function runAfterJQ() {

		$('.my-colorpicker2').colorpicker();
		
	}
</script>