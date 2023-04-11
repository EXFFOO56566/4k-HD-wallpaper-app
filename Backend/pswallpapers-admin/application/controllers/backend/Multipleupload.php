<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * Likes Controller
 */

class Multipleupload extends BE_Controller {
		/**
	 * Construt required variables
	 */
	function __construct() {

		parent::__construct( MODULE_CONTROL, 'Mass Upload' );
		$this->load->library('uploader');
		$this->load->library('csvimport');
	}

	/**
	 * Load Api Key Entry Form
	 */

	function index( ) {

		
		$this->load_form($this->data);

	}

	

	function upload($id = false) {
		
		if ( $this->is_POST()) {

			// If file upload form submitted
	        if(!empty($_FILES['images']['name'])){
	            
	        	//Start Prepare Data

	        	$data = array();
	        	// prepare wallpaper_name
				if ( $this->has_data( 'wallpaper_name' )) {
					$data['wallpaper_name'] = $this->get_data( 'wallpaper_name' );
				}

				// cat_id
				if ( $this->has_data( 'cat_id' )) {
					$data['cat_id'] = $this->get_data( 'cat_id' );
				}

				// prepare wallpaper_search_tags
				if ( $this->has_data( 'wallpaper_search_tags' )) {
					$data['wallpaper_search_tags'] = $this->get_data( 'wallpaper_search_tags' );
				}

				// prepare types
				if ( $this->has_data( 'types' )) {
					$data['types'] = $this->get_data( 'types' );
				}

				// prepare point
				if ( $this->has_data( 'point' )) {
					$data['point'] = $this->get_data( 'point' );
				}



				// prepare point
				if ( $this->has_data( 'color_id' )) {
					$data['color_id'] = $this->get_data( 'color_id' );
				}
				
				// if 'is published' is checked,
				if ( $this->has_data( 'wallpaper_is_published' )) {

					$data['wallpaper_is_published'] = 1;
				} else {
					
					$data['wallpaper_is_published'] = 0;
				}

				// if 'is published' is checked,
				if ( $this->has_data( 'is_recommended' )) {
					
					$data['is_recommended'] = 1;
					if ($data['is_recommended'] == 1) {

						if($this->get_data( 'is_recommended_stage' ) == $this->has_data( 'is_recommended' )) {
							$data['updated_date'] = date("Y-m-d H:i:s");
						} else {
							$data['recommended_date'] = date("Y-m-d H:i:s");
							$data['updated_date'] = $data['recommended_date'];
							
						}
					}
				} else {
					
					$data['is_recommended'] = 0;
				}

				$data['updated_date'] = date("Y-m-d H:i:s");
				$data['is_batch_upload'] = 1;
				$data['is_wallpaper'] = 1;

				// credit
				if ( $this->has_data( 'credit' )) {
					$data['credit'] = $this->get_data( 'credit' );
				}


				//User ID 
				$logged_in_user = $this->ps_auth->get_user_info();
				$data['added_user_id'] = $logged_in_user->user_id;
            	
            	//End Prepare Data
	            $filesCount = count($_FILES['images']['name']);
	       
	            for($i = 0; $i < $filesCount; $i++){
	            	//File Extension
					$extension = pathinfo($_FILES['images']['name'][$i], PATHINFO_EXTENSION);
					
					if ($extension == "gif") {
						$data["is_gif"] = 1;
						$data['is_wallpaper'] = 0;
					}
	            	// save wallpaper
					if ( ! $this->Wallpaper->save( $data, $id )) {
						
					// if there is an error in inserting user data,	
						// rollback the transaction
						$this->db->trans_rollback();

						// set error message
						$this->data['error'] = get_msg( 'err_model' );
						
						return;
					}
				
					if ( $data['wallpaper_id'] != "" ) {
					// if id is false, this is adding new record
						
						$_FILES['file']['name']     = $_FILES['images']['name'][$i];
		                $_FILES['file']['type']     = $_FILES['images']['type'][$i];
		                $_FILES['file']['tmp_name'] = $_FILES['images']['tmp_name'][$i];
		                $_FILES['file']['error']    = $_FILES['images']['error'][$i];
		                $_FILES['file']['size']     = $_FILES['images']['size'][$i];
		                
		                // File upload configuration
		                $config['upload_path'] = $this->config->item('upload_path');
		                $config['allowed_types'] = $this->config->item('image_type');
		               
		                
		                // Load and initialize upload library
		                $this->load->library('upload', $config);
		                $this->upload->initialize($config);
		                
		                // Upload file to server
		                if($this->upload->do_upload('file')){
		                	
		                    // Uploaded file data
		                    $uploaded_data = $this->upload->data();

		                    //$img_name = $uploaded_data['raw_name'] .'.png';

		                    $img_name = $uploaded_data['raw_name'] .'.' . $extension;
		                    $image_path = $uploaded_data['file_path'] . $img_name;

		                    if(strtolower($extension) != "gif") {

		                    	
								$thumb_width  =   round($uploaded_data['image_width'] * 0.25, 0);
								$thumb_height =   round($uploaded_data['image_height'] * 0.25, 0);
								$this->ps_image->create_thumbnail( $image_path,$thumb_width,$thumb_height   );

		                    } else {

		                    	$img_name = $uploaded_data['raw_name'] .'.' . "gif";
		                    	$image_path = $uploaded_data['file_path'] . $img_name;

		                    	$img = $uploaded_data['raw_name'];
		                    	$newpng = "$img.png";
		                    	
		                    	$png = imagepng(imagecreatefromgif( $image_path ), $uploaded_data['file_path'] .'/thumbnail/' . $newpng);

		                    }


							 // prepare image data
							$image = array(
								'img_parent_id'	=> $data['wallpaper_id'],
								'img_type' 		=> "wallpaper",
								'img_desc' 		=> "",
								'img_path' 		=> $uploaded_data['file_name'],
								'img_width'		=> $uploaded_data['image_width'],
								'img_height'	=> $uploaded_data['image_height']
							);

							if ($image['img_width'] > $image['img_height']) {
								$wp_data['is_landscape'] = 1;
								$wp_data['is_portrait'] = 0;
								$wp_data['is_square'] = 0;
								$this->Wallpaper->save( $wp_data, $data['wallpaper_id']);
							} else if ($image['img_width'] < $image['img_height']) {
								$wp_data['is_portrait'] = 1;
								$wp_data['is_landscape'] = 0;
								$wp_data['is_square'] = 0;
								$this->Wallpaper->save( $wp_data, $data['wallpaper_id']);
							} else if ($image['img_width'] == $image['img_height']) {
								$wp_data['is_square'] = 1;
								$wp_data['is_landscape'] = 0;
								$wp_data['is_portrait'] = 0;
								$this->Wallpaper->save( $wp_data, $data['wallpaper_id']);
							}
							
							// save image 
							if ( ! $this->Image->save( $image )) {
							// if error in saving image
								// set error message
								$this->data['error'] = get_msg( 'err_model' );
								
								return false;
							}

							
		                }

					}


					//End - Images Upload 

					/** 
					 * Check Transactions 
					 */

					// commit the transaction
					if ( ! $this->check_trans()) {
						// set flash error message
						$this->set_flash_msg( 'error', get_msg( 'err_model' ));
					} else {

						if ( $id ) {
						// if user id is not false, show success_add message
							$this->set_flash_msg( 'success', get_msg( 'success_color_edit' ));
						} else {
						// if user id is false, show success_edit message
							$this->set_flash_msg( 'success', get_msg( 'success_color_add' ));
						}
					}


	            }
	            
	           

	            redirect( site_url("/admin/wallpapers"));

	        }



		} else {
			$this->load_form($this->data);
		}
		

	}

}