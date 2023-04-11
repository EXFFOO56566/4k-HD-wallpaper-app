<script>

	<?php if ( $this->config->item( 'client_side_validation' ) == true ): ?>

	function jqvalidate() {

		$('#wallpaper-form').validate({
			rules:{
				wallpaper_name:{
					blankCheck : "",
					minlength: 3,
				},
				cat_id: {
		       		indexCheck : ""
		      	},
		      	modes: {
		       		indexCheck : ""
		      	},
		      	types: {
		       		indexCheck : ""
		      	},
				images1: {
		       		fileChecking : ""
		      	},
		      	video: {
		       		fileCheckings : ""
		      	},
		      	icon:{
					required: true
				},
				point: {
		       		pointChecking : ""
		      	}
			},
			messages:{
				wallpaper_name:{
					blankCheck: "<?php echo get_msg( 'err_wallpaper_name' ) ;?>",
					minlength: "<?php echo get_msg( 'err_wallpaper_len' ) ;?>",
					remote: "<?php echo get_msg( 'err_wallpaper_exist' ) ;?>."
				},
				cat_id:{
			       indexCheck: "Please Select Category Name."
			    }, 
			    modes:{
			       indexCheck: "Please Select Modes."
			    },
			    types:{
			       indexCheck: "Please Select Types."
			    },
				images1:{
					fileChecking: "Please Upload Photo."
				},
				video:{
					fileCheckings: "Please Upload Video."
				},
				icon:{
					required: "Please File Upload Icon."
				},
				point:{
					pointChecking : "Please Fill Point."
				}
			}
		});
		// custom validation
		jQuery.validator.addMethod("indexCheck",function( value, element ) {

			if(value == "") {
		    	return false;
		  	} else {
		   	 	return true;
		   	}
			   
		});

		jQuery.validator.addMethod("blankCheck",function( value, element ) {
			
			   if(value == "") {
			    	return false;
			   } else {
			   	 	return true;
			   }
		});

		jQuery.validator.addMethod("fileChecking",function(  ) {
			
			   //alert($("#images1").val());
			   if($("#images1").val() == "") {
			   			
			   		return false;

			   } else {

			   		var fileExtension = ['gif'];
			        if ($.inArray($("#images1").val().split('.').pop().toLowerCase(), fileExtension) == -1) {
			        	$('#is_gif').val('0');
			        	return true;
			            //alert("Only formats are allowed : "+fileExtension.join(', '));
			        } else {

			        	$('#is_gif').val(1);
			        	//alert($('#is_gif').val());
			        	$("#point").val(0);
			        	$("#point").attr("disabled", "disabled");
			   			return true;

			   		}
			   }

		});

		jQuery.validator.addMethod("fileCheckings",function(  ) {
			
			   //alert($("#images1").val());
			   if($("#video").val() == "") {
			   			
			   		return false;

			   } else {

			   		var fileExtension = ['mp4'];
			        if ($.inArray($("#video").val().split('.').pop().toLowerCase(), fileExtension) == -1) {
			        	$('#is_video_wallpaper').val('0');
			        	return true;
			            //alert("Only formats are allowed : "+fileExtension.join(', '));
			        } else {

			        	$('#is_video_wallpaper').val(1);
			        	//alert($('#is_gif').val());
			        	$("#point").val(0);
			        	$("#point").attr("disabled", "disabled");
			   			return true;

			   		}
			   }

		});

		jQuery.validator.addMethod("pointChecking",function( value, element ){


			   if($("#types").val() == 2) {
			   		
			   		if(value == "" || value == 0) {
			   			return false;
			   		} else {
			   			return true;
			   		}

			   } else {
			   		return true;
			   }

		});
	}

	<?php endif; ?>

	function runAfterJQ() {

		$('.delete-img').click(function(e){
			e.preventDefault();

			// get id and image
			var id = $(this).attr('id');

			// do action
			var action = '<?php echo $module_site_url .'/delete_cover_photo/'; ?>' + id + '/<?php echo @$wallpaper->wallpaper_id; ?>';
			console.log( action );
			$('.btn-delete-image').attr('href', action);
		});

		$('.delete-video').click(function(e){
			e.preventDefault();

			// get id and image
			var id = $(this).attr('id');

			// do action
			var action = '<?php echo $module_site_url .'/delete_video/'; ?>' + id + '/<?php echo @$wallpaper->wallpaper_id; ?>';
			console.log( action );
			$('.btn-delete-video').attr('href', action);
		});

		$('.image-popup-vertical-fit').magnificPopup({
			type: 'image',
			closeOnContentClick: true,
			mainClass: 'mfp-img-mobile',
			image: {
				verticalFit: true
			}
			
		});

		$(document).ready(function() {
			$('.thumbnail').magnificPopup({
				delegate: 'a',
				type: 'image',
			});
		});

		$("#types").change(function () {
			    if(this.value==1) { //3rd radiobutton
		            $("#point").attr("disabled", "disabled"); 
		            $("#point").val(0);
		        }
		        else {
		            $("#point").removeAttr("disabled"); 
		        }
		});

		$("#is_gif").change(function () {
			    if(this.value=="accept") { //3rd radiobutton
		            $("#point").attr("disabled", "disabled"); 
		            $("#point").val(0);
		        }
		        else {
		            $("#point").removeAttr("disabled"); 
		        }
		});
	

		$('input[name="point"]').keyup(function(e)
	                                {
		  if (/\D/g.test(this.value))
		  {
		    // Filter non-digits from input value.
		    this.value = this.value.replace(/\D/g, '');
		  }
		});

	}
</script>

<?php 
	// replace cover photo modal
	$data = array(
		'title' => get_msg('upload_photo'),
		'img_type' => 'wallpaper',
		'img_parent_id' => @$wallpaper->wallpaper_id
	);

	$this->load->view( $template_path .'/components/photo_upload_modal', $data );

	// delete cover photo modal
	$this->load->view( $template_path .'/components/delete_cover_photo_modal' ); 

	$data = array(
		'title' => get_msg('upload_photo'),
		'img_type' => 'video-icon',
		'img_parent_id' => @$wallpaper->wallpaper_id
	);


	$this->load->view( $template_path .'/components/video_icon_upload_modal', $data );

	// delete cover photo modal
	$this->load->view( $template_path .'/components/delete_cover_photo_modal' ); 

	// replace cover photo modal
	$data = array(
		'title' => get_msg('upload_video'),
		'img_type' => 'video',
		'img_parent_id' => @$wallpaper->wallpaper_id
	);

	$this->load->view( $template_path .'/components/video_upload_modal', $data );

	// delete cover photo modal
	$this->load->view( $template_path .'/components/delete_video_modal' ); 
?>

<style type="text/css">
    
    .box{
        color: #000;
        padding: 20px;
        display: none;
        margin-top: 20px;
    }
    
    .red{ background: #ff0000; }
    .green{ background: #228B22; }
    .blue{ background: #0000ff; }
    .per_order_based_enabled { background: #e2e0e0; }
    .per_item_based_enabled { background: #e2e0e0; }
    .free_enabled { background: #e2e0e0; }


    label{ margin-right: 15px; }

</style>


<script type="text/javascript">
$(document).ready(function(){
    $('input[type="radio"]').click(function(){
        var inputValue = $(this).attr("value");
        var targetBox = $("." + inputValue);
        $(".box").not(targetBox).hide();
        $(targetBox).show();
    });
});
</script>