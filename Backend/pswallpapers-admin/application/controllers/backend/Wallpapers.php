<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * Wallpapers Controller
 */
class Wallpapers extends BE_Controller {

	/**
	 * Construt required variables
	 */
	function __construct() {

		parent::__construct( MODULE_CONTROL, 'WALLPAPERS' );
	}

	/**
	 * List down the wallpapers
	 */
	function index() {
		// no publish filter
		$conds['no_publish_unpublish_filter'] = 1;
		$conds['order_by_field'] = "added_date";
		$conds['order_by_type'] = "desc";	

		// get rows count
		$this->data['rows_count'] = $this->Wallpaper->count_all_by( $conds );

		// get wallpapers
		$wallpapers = $this->Wallpaper->get_all_by( $conds , $this->pag['per_page'], $this->uri->segment( 4 ) );

		$this->data['wallpapers'] = $wallpapers;

		// load index logic
		parent::index();
	}

	/**
	 * Searches for the first match.
	 */
	function search() {
		
		// breadcrumb urls
		$this->data['action_title'] = get_msg( 'wallpaper_search' );

		if($this->input->post('submit') != NULL ){

			// echo "aaaaaa";die;


			// condition with search term
			$conds = array( 'searchterm' => $this->searchterm_handler( $this->input->post( 'searchterm' )));
			// print_r($this->input->post( 'searchterm' ));die;
			if($this->input->post( 'searchterm' ) != "") {
				$conds['searchterm'] = $this->input->post( 'searchterm' );
				$this->data['searchterm'] = $this->input->post( 'searchterm' );
				$this->session->set_userdata(array("searchterm" => $this->input->post('searchterm')));
			} else {
				$this->session->set_userdata(array("searchterm" => NULL ));
			}
			
			if($this->input->post('is_portrait') == "is_portrait") {
				$conds['is_portrait'] = '1';
				$this->data['is_portrait'] = '1';
				$this->session->set_userdata(array("is_portrait" => '1'));
			} else {
				
				$this->session->set_userdata(array("is_portrait" => '0'));
			}

			if($this->input->post('is_recommended') == "is_recommended") {
				$conds['is_recommended'] = '1';
				$this->data['is_recommended'] = '1';
				$this->session->set_userdata(array("is_recommended" => '1'));
			} else {
				
				$this->session->set_userdata(array("is_recommended" => NULL));
			}

			if($this->input->post('is_landscape') == "is_landscape") {
				$conds['is_landscape'] = '1';
				$this->data['is_landscape'] = '1';
				$this->session->set_userdata(array("is_landscape" => '1'));
			} else {
				
				$this->session->set_userdata(array("is_landscape" => '0'));
			}

			if($this->input->post('is_square') == "is_square") {
				$conds['is_square'] = '1';
				$this->data['is_square'] = '1';
				$this->session->set_userdata(array("is_square" => '1'));
			} else {
			
				$this->session->set_userdata(array("is_square" => '0'));
			}

			if($this->input->post('is_gif') == "is_gif") {
				$conds['is_gif'] = '1';
				$this->data['is_gif'] = '1';
				$this->session->set_userdata(array("is_gif" => '1'));
			} else {
				
				$this->session->set_userdata(array("is_gif" => '0'));
			}

			if($this->input->post('is_video_wallpaper') == "is_video_wallpaper") {
				$conds['is_video_wallpaper'] = '1';
				$this->data['is_video_wallpaper'] = '1';
				$this->session->set_userdata(array("is_video_wallpaper" => '1'));
			} else {
				
				$this->session->set_userdata(array("is_video_wallpaper" => '0'));
			}

			
			if($this->input->post('cat_id') != ""  || $this->input->post('cat_id') != '0') {
				$conds['cat_id'] = $this->input->post('cat_id');
				$this->data['cat_id'] = $this->input->post('cat_id');
				$this->session->set_userdata(array("cat_id" => $this->input->post('cat_id')));
			} else {
				$this->session->set_userdata(array("cat_id" => NULL ));
			}

			if($this->input->post('color_id') != "" || $this->input->post('color_id') != '0') {
				$conds['color_id'] = $this->input->post('color_id');
				$this->data['color_id'] = $this->input->post('color_id');
				$this->session->set_userdata(array("color_id" => $this->input->post('color_id')));
			} else {
				$this->session->set_userdata(array("color_id" => NULL ));
			}

			if($this->input->post('types') != "" || $this->input->post('types') != '0') {
				$conds['types'] = $this->input->post('types');
				$this->data['types'] = $this->input->post('types');
				$this->session->set_userdata(array("types" => $this->input->post('types')));
			} else {
				$this->session->set_userdata(array("types" => NULL ));
			}

			if($this->input->post('point_min') != "") {
				$conds['point_min'] = $this->input->post('point_min');
				$this->data['point_min'] = $this->input->post('point_min');
				$this->session->set_userdata(array("point_min" => $this->input->post('point_min')));
			} else {
				$this->session->set_userdata(array("point_min" => NULL ));
			}

			if($this->input->post('point_max') != "") {
				$conds['point_max'] = $this->input->post('point_max');
				$this->data['point_max'] = $this->input->post('point_max');
				$this->session->set_userdata(array("point_max" => $this->input->post('point_max')));
			} else {
				$this->session->set_userdata(array("point_max" => NULL ));
			}

			//Order By 

			$conds['no_publish_unpublish_filter'] = 1;
			$conds['order_by'] = 1;

			if($this->input->post('order_by') == "added_date_asc") {

				$conds['order_by_field'] = "added_date";
				$conds['order_by_type'] = "asc";
				$this->data['order_by'] = $this->input->post('order_by');
				$this->session->set_userdata(array("order_by" => $this->input->post('order_by')));
			
			}  

			if($this->input->post('order_by') == "added_date_desc") {
				
				$conds['order_by_field'] = "added_date";
				$conds['order_by_type'] = "desc";

				$this->data['order_by'] = $this->input->post('order_by');
				$this->session->set_userdata(array("order_by" => $this->input->post('order_by')));
			
			}  

			if($this->input->post('order_by') == "name_asc") {
				
				$conds['order_by_field'] = "wallpaper_name";
				$conds['order_by_type'] = "asc";

				$this->data['order_by'] = $this->input->post('order_by');
				$this->session->set_userdata(array("order_by" => $this->input->post('order_by')));
			
			}  

			if($this->input->post('order_by') == "name_desc") {
				
				$conds['order_by_field'] = "wallpaper_name";
				$conds['order_by_type'] = "desc";

				$this->data['order_by'] = $this->input->post('order_by');
				$this->session->set_userdata(array("order_by" => $this->input->post('order_by')));

			
			} 

			if($this->input->post('order_by') == "point_asc") {
				
				$conds['order_by_field'] = "point";
				$conds['order_by_type'] = "asc";

				$this->data['order_by'] = $this->input->post('order_by');
				$this->session->set_userdata(array("order_by" => $this->input->post('order_by')));
			
			} 

			if($this->input->post('order_by') == "point_desc") {
				
				$conds['order_by_field'] = "point";
				$conds['order_by_type'] = "desc";

				$this->data['order_by'] = $this->input->post('order_by');
				$this->session->set_userdata(array("order_by" => $this->input->post('order_by')));
			
			}

		} else {
			//read from session value
			if($this->session->userdata('searchterm') != NULL) {
				$conds['searchterm'] = $this->session->userdata('searchterm');
				$this->data['searchterm'] = $this->session->userdata('searchterm');
			}
					
			if($this->session->userdata('is_portrait') != NULL){
				$conds['is_portrait'] = $this->session->userdata('is_portrait');
				$this->data['is_portrait'] = $this->session->userdata('is_portrait');
			}

			if($this->session->userdata('is_recommended') != NULL){
				$conds['is_recommended'] = $this->session->userdata('is_recommended');
				$this->data['is_recommended'] = $this->session->userdata('is_recommended');
			}

			if($this->session->userdata('is_landscape') != NULL){
				$conds['is_landscape'] = $this->session->userdata('is_landscape');
				$this->data['is_landscape'] = $this->session->userdata('is_landscape');
			}

			if($this->session->userdata('is_square') != NULL){
				$conds['is_square'] = $this->session->userdata('is_square');
				$this->data['is_square'] = $this->session->userdata('is_square');
			}

			if($this->session->userdata('is_gif') != NULL){
				$conds['is_gif'] = $this->session->userdata('is_gif');
				$this->data['is_gif'] = $this->session->userdata('is_gif');
			}

			if($this->session->userdata('is_video_wallpaper') != NULL){
				$conds['is_video_wallpaper'] = $this->session->userdata('is_video_wallpaper');
				$this->data['is_video_wallpaper'] = $this->session->userdata('is_video_wallpaper');
			}

			if($this->session->userdata('cat_id') != NULL){
				$conds['cat_id'] = $this->session->userdata('cat_id');
				$this->data['cat_id'] = $this->session->userdata('cat_id');
			}

			if($this->session->userdata('color_id') != NULL){
				$conds['color_id'] = $this->session->userdata('color_id');
				$this->data['color_id'] = $this->session->userdata('color_id');
			}

			if($this->session->userdata('types') != NULL){
				$conds['types'] = $this->session->userdata('types');
				$this->data['types'] = $this->session->userdata('types');
			}

			if($this->session->userdata('point_min') != NULL){
				$conds['point_min'] = $this->session->userdata('point_min');
				$this->data['point_min'] = $this->session->userdata('point_min');
			}			

			if($this->session->userdata('point_max') != NULL){
				$conds['point_max'] = $this->session->userdata('point_max');
				$this->data['point_max'] = $this->session->userdata('point_max');
			}


			//Order By
			$conds['no_publish_unpublish_filter'] = 1;
			$conds['order_by'] = 1;

			if($this->session->userdata('order_by') != NULL){
				
				if($this->session->userdata('order_by') == "added_date_asc") {

					$conds['order_by_field'] = "added_date";
					$conds['order_by_type'] = "asc";
					$this->data['order_by'] = $this->input->post('order_by');
					
				}

				if($this->session->userdata('order_by') == "added_date_desc") {
					
					$conds['order_by_field'] = "added_date";
					$conds['order_by_type'] = "desc";

					$this->data['order_by'] = $this->input->post('order_by');
					
				}

				if($this->session->userdata('order_by') == "name_asc") {
				
					$conds['order_by_field'] = "name";
					$conds['order_by_type'] = "asc";

					$this->data['order_by'] = $this->input->post('order_by');
					
				
				}  

				if($this->session->userdata('order_by') == "name_desc") {
				
					$conds['order_by_field'] = "name";
					$conds['order_by_type'] = "desc";

					$this->data['order_by'] = $this->input->post('order_by');
				
				} 


				if($this->session->userdata('order_by')  == "point_asc") {
				
					$conds['order_by_field'] = "point";
					$conds['order_by_type'] = "asc";

					$this->data['order_by'] = $this->input->post('order_by');
				
				} 

				if($this->session->userdata('order_by') == "point_desc") {
				
					$conds['order_by_field'] = "point";
					$conds['order_by_type'] = "desc";

					$this->data['order_by'] = $this->input->post('order_by');
				
				}

			}  


		}

		if ($conds['order_by_field'] == "" ){
			$conds['order_by_field'] = "added_date";
			$conds['order_by_type'] = "desc";
		}
		// pagination
		$this->data['rows_count'] = $this->Wallpaper->count_all_by( $conds );

		//print_r($conds); die;
		// search data
		$this->data['wallpapers'] = $this->Wallpaper->get_all_by( $conds, $this->pag['per_page'], $this->uri->segment( 4 ) );
		
		// load add list
		parent::search();
	}

	/**
	 * Create new one
	 */
	function add() {

		// breadcrumb urls
		$this->data['action_title'] = get_msg( 'wallpaper_add' );

		// call the core add logic
		parent::add();
	}

	/**
	 * Update the existing one
	 */
	function edit( $id ) {

		// breadcrumb urls
		$this->data['action_title'] = get_msg( 'wallpaper_edit' );

		// load user
		$wallpaper = $this->Wallpaper->get_one( $id );

		$this->data['wallpaper'] = $wallpaper;

		// call the parent edit logic
		parent::edit( $id );
	}

	/**
	 * Saving Logic
	 * 1) upload image
	 * 2) save wallpaper
	 * 3) save image
	 * 4) check transaction status
	 *
	 * @param      boolean  $id  The user identifier
	 */
	function save( $id = false ) {

		// start the transaction
		$this->db->trans_start();
		
		$logged_in_user = $this->ps_auth->get_user_info();

		/** 
		 * Insert Wallpaper Records 
		 */
		$data = array();
		//print_r($_POST);die;



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
		} else {
			$data['point'] = 0;
		}

		// prepare color
		if ( $this->has_data( 'color_id' )) {
			$data['color_id'] = $this->get_data( 'color_id' );
		}

		// nothing to upload

		$wallpaperRadio = $this->input->post('wallpaperRadio');
		//print_r($wallpaperRadio);die;

	
		// if 'is_wallpaper' is checked,
		if ($this->input->post('wallpaperRadio') == 'is_wallpaper') {
			$data['is_wallpaper'] = 1;
			$data['is_video_wallpaper'] = 0;
		}

		// if 'is_video_wallpaper' is checked,	
		if ($this->input->post('wallpaperRadio') == 'is_video_wallpaper') {
			$data['is_wallpaper'] = 0;
			$data['is_video_wallpaper'] = 1;
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


		if ($id == "") {
			$data['added_user_id'] = $logged_in_user->user_id;
		}

		//For Edit Case
		if($id) {
			$data['updated_user_id'] = $logged_in_user->user_id;

			$current_added_user_id = $this->Wallpaper->get_one($id)->added_user_id;

			if($current_added_user_id != $logged_in_user->user_id) {
				//keep existing added_user_id because it is user upload from mobile app
				$data['added_user_id'] = $current_added_user_id;
			}

		} 

		// credit
		if ( $this->has_data( 'credit' )) {
			$data['credit'] = $this->get_data( 'credit' );
		}

		if ($id == "") {
			if ($this->get_data( 'is_gif' ) == 1 ) {
				$data['is_gif'] = 1;
				
			} elseif ($this->get_data( 'is_gif' ) == 0 ) {
				$data['is_gif'] = 0;
			}
		} 

		if ($data['is_gif'] == 1) {
			$data['is_wallpaper'] = 0;
		}

		if ( $this->check_trans()) {
        	
			if (empty($wallpaperRadio)) {

				$this->set_flash_msg( 'error', get_msg( 'upload_wallpaper' ));
				redirect( $this->module_site_url() . '/add' );
			}
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
		//print_r($_FILES['images1']['tmp_name']);die;
		//print_r($_FILES);die;
	

		/** 
		 * Upload Image Records 
		 */
		if ( !$id ) {
		// if id is false, this is adding new record

			if($data['is_wallpaper'] == 1 || $data['is_gif'] == 1){
				// normal wallpaper upload (include gif)
				if ( ! $this->insert_images( $_FILES, 'wallpaper', $data['wallpaper_id'] )) {
				// if error in saving image

					// commit the transaction
					$this->db->trans_rollback();
					
					return;
				}

			} elseif($data['is_video_wallpaper'] == 1){
			// video wallpaper upload

				if ( ! $this->insert_videos( $_FILES, 'video', $data['wallpaper_id'] )) {
					// if error in saving image

					// commit the transaction
					$this->db->trans_rollback();
					
					return;
				}

				if ( ! $this->insert_videos( $_FILES, 'video-icon', $data['wallpaper_id'] )) {
				// if error in saving image

					// commit the transaction
					$this->db->trans_rollback();
					
					return;
				}

			}	

		}

		//print_r($images);die;

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

		redirect( $this->module_site_url());
	}

	/**
	 * Delete all the wallpaper under wallpaper
	 *
	 * @param      integer  $wallpaper_id  The wallpaper identifier
	 */
	function delete_all( $wallpaper_id = 0 )
	{
		// start the transaction
		$this->db->trans_start();

		// check access
		$this->check_access( DEL );
		
		// delete wallpapers and images
		$enable_trigger = true; 

		$type = "wallpaper";

		/** Note: enable trigger will delete wallpaper under wallpaper and all wallpaper related data */
		if ( !$this->ps_delete->delete_history( $wallpaper_id, $type, $enable_trigger )) {
		// if error in deleting wallpaper,

			// set error message
			$this->set_flash_msg( 'error', get_msg( 'err_model' ));

			// rollback
			$this->trans_rollback();

			// redirect to list view
			redirect( $this->module_site_url());
		}
			
		/**
		 * Check Transwallpaperion Status
		 */
		if ( !$this->check_trans()) {

			$this->set_flash_msg( 'error', get_msg( 'err_model' ));	
		} else {
        	
			$this->set_flash_msg( 'success', get_msg( 'success_wallpaper_delete' ));
		}
		
		redirect( $this->module_site_url());
	}

	/**
	 * Determines if valid input.
	 *
	 * @return     boolean  True if valid input, False otherwise.
	 */
	function is_valid_input( $wallpaper_id = 0 ) {
		
		$rule = 'required|callback_is_valid_name['. $wallpaper_id  .']';
		
		$this->form_validation->set_rules( 'wallpaper_name', get_msg( 'wallpaper_name' ), $rule);

		if ( $this->form_validation->run() == FALSE ) {
		// if there is an error in validating,

			return false;
		}

		return true;
	}

	/**
	 * Determines if valid name.
	 *
	 * @param      <type>   $name  The  name
	 * @param      integer  $id     The  identifier
	 *
	 * @return     boolean  True if valid name, False otherwise.
	 */
	function is_valid_name( $name, $wallpaper_id = 0 )
	{		
		$conds['cat_id'] = $_POST['cat_id'];
		$conds['wallpaper_name'] = $name;

		 	if( $wallpaper_id != "") {
		 		
				if ( strtolower( $this->Wallpaper->get_one( $wallpaper_id )->wallpaper_name ) == strtolower( $name )) {
				// if the name is existing name for that user id,
					return true;
				} else if ( $this->Wallpaper->exists( ($conds ))) {
				// if the name is existed in the system,
					$this->form_validation->set_message('is_valid_name', get_msg( 'err_dup_name' ));
					return false;
				}
			} else {
				
				if ( $this->Wallpaper->exists( ($conds ))) {
				// if the name is existed in the system,
					$this->form_validation->set_message('is_valid_name', get_msg( 'err_dup_name' ));
					return false;
				}
			}
			
			return true;
	}

	/**
	 * Publish the record
	 *
	 * @param      integer  $wallpaper_id  The wallpaper identifier
	 */
	function ajx_publish( $wallpaper_id = 0 )
	{
		// check access
		$this->check_access( PUBLISH );
		
		// prepare data
		$wallpaper_data = array( 'wallpaper_is_published'=> 1 );
			
		// save data
		if ( $this->Wallpaper->save( $wallpaper_data, $wallpaper_id )) {
			//Need to delete at history table because that wallpaper need to show again on app
			$data_delete['wallpaper_id'] = $wallpaper_id;
			$this->Wallpaper_delete->delete_by($data_delete);

			echo 'true';
		} else {
			echo 'false';
		}
	}
	
	/**
	 * Unpublish the records
	 *
	 * @param      integer  $wallpaper_id  The wallpaper identifier
	 */
	function ajx_unpublish( $wallpaper_id = 0 )
	{
		// check access
		$this->check_access( PUBLISH );
		
		// prepare data
		$wallpaper_data = array( 'wallpaper_is_published'=> 0 );
			
		// save data
		if ( $this->Wallpaper->save( $wallpaper_data, $wallpaper_id )) {
			//Need to save at history table because that wallpaper no need to show on app
			$data_delete['wallpaper_id'] = $wallpaper_id;
			$this->Wallpaper_delete->save($data_delete);

			echo 'true';
		} else {
			echo 'false';
		}
	}
}