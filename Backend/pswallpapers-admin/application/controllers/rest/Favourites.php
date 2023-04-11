<?php
require_once( APPPATH .'libraries/REST_Controller.php' );

/**
 * REST API for Touches
 */
class Favourites extends API_Controller
{

	/**
	 * Constructs Parent Constructor
	 */
	function __construct()
	{
		$is_login_user_nullable = true;

		// call the parent
		parent::__construct( 'Favourite', $is_login_user_nullable );

		// set the validation rules for create and update
		$this->validation_rules();
	}

	/**
	 * Determines if valid input.
	 */
	function validation_rules()
	{
		// validation rules for create
		$this->create_validation_rules = array(
			array(
	        	'field' => 'wallpaper_id',
	        	'rules' => 'required'
	        ),
	        array(
	        	'field' => 'user_id',
	        	'rules' => 'required'
	        )
        );

	}

/**
	* When user press like button from app (fav wallpaper)
	*/
	function press_post() 
	{

		// validation rules for create
		$rules = array(
			array(
	        	'field' => 'wallpaper_id',
	        	'rules' => 'required|callback_id_check[Wallpaper]'
	        ),
	        array(
	        	'field' => 'user_id',
	        	'rules' => 'required|callback_id_check[User]'
	        )
        );

		// validation
        if ( !$this->is_valid( $rules )) exit;

        // for wallpaper favourite
		$wallpaper_id = $this->post('wallpaper_id');
		$user_id = $this->post('user_id');

		// prep data
        $data = array( 'wallpaper_id' => $wallpaper_id, 'user_id' => $user_id );

		if ( $this->Favourite->exists( $data )) {

			if ( !$this->Favourite->delete_by( $data )) {

				$this->error_response( get_msg( 'err_model' ));
			} else {
				//Need to update download_count at wallpaper table 
				$data_wallpaper['wallpaper_id'] = $wallpaper_id;
				
				//Get Downlaod Count from Download Table
				$wallpaper_favourite_count = $this->Favourite->count_all_by($data_wallpaper);

				//Update at Wallpaper Table
				$data_wallpaper['favourite_count'] = $wallpaper_favourite_count;
				$this->Wallpaper->save($data_wallpaper, $wallpaper_id);
			} 

		} else {
			if ( !$this->Favourite->save( $data )) {
				$this->error_response( get_msg( 'err_model' ));
			} else {
				//Need to update download_count at wallpaper table 
				$data_wallpaper['wallpaper_id'] = $wallpaper_id;
				
				//Get Downlaod Count from Download Table
				$wallpaper_favourite_count = $this->Favourite->count_all_by($data_wallpaper);

				//Update at Wallpaper Table
				$data_wallpaper['favourite_count'] = $wallpaper_favourite_count;
				$this->Wallpaper->save($data_wallpaper, $wallpaper_id);
			}
		}

		$obj = new stdClass;
		$obj->id = $wallpaper_id;
		$wallpaper = $this->Wallpaper->get_one( $obj->id );
		
		$wallpaper->login_user_id_post = $user_id;
		$this->ps_adapter->convert_wallpaper( $wallpaper );
		$this->custom_response( $wallpaper );

		

	}

	
}