<?php
require_once( APPPATH .'libraries/REST_Controller.php' );

/**
 * REST API for Touches
 */
class Touches extends API_Controller
{

	/**
	 * Constructs Parent Constructor
	 */
	function __construct()
	{
		$is_login_user_nullable = true;

		// call the parent
		parent::__construct( 'Touch', $is_login_user_nullable );

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
	        )
        );

	}

	/**
	 * Convert Object
	 */
	function convert_object( &$obj )
	{
		// if ( $this->is_add ) {
		// 	$this->success_response( get_msg( 'success_touch_count '));
		// } else {
		// 	$this->error_response( get_msg( 'err_touch_count' ));
		// }

		// call parent convert object
		parent::convert_object( $obj );

		// convert customize product object
		$this->ps_adapter->convert_wallpaper( $obj );
	}
}