<?php
require_once( APPPATH .'libraries/REST_Controller.php' );

/**
 * REST API for Notification
 */
class Notis extends API_Controller
{
	/**
	 * Constructs Parent Constructor
	 */
	function __construct()
	{
		// call the parent
		parent::__construct( 'Noti' );

	}

	/**
	* Register Device
	*/
	function register_post()
	{
		// validation rules for user register
		$rules = array(
			array(
	        	'field' => 'platform_name',
	        	'rules' => 'required'
	        ),
	        array(
	        	'field' => 'device_id',
	        	'rules' => 'required'
	        )
        );

		// exit if there is an error in validation,
        if ( !$this->is_valid( $rules )) exit;

        if($this->post('platform_name') == "android") {

        	$noti_data = array(
	        	"device_id" => $this->post('device_id'), 
	        	"os_type" => "ANDROID"
        	);

        } else {

        	$noti_data = array(
	        	"device_id" => $this->post('device_id'), 
	        	"os_type" => "IOS"
        	);
        }

        if ( $this->Noti->exists( $noti_data )) {
        // if the noti data is already existed, return success

        	$this->success_response( get_msg( 'success_noti_register '));
        }

        if ( !$this->Noti->save( $noti_data )) {
        // if there is error in inserting noti data, return error

        	$this->error_response( get_msg( 'err_noti_register' ));
        }

        // else, return success
        $this->success_response( get_msg( 'success_noti_register '));
	}

	/**
	* Register Device
	*/
	function unregister_post()
	{
		// validation rules for user register
		$rules = array(
			array(
	        	'field' => 'device_id',
	        	'rules' => 'required'
	        )
        );

		// exit if there is an error in validation,
        if ( !$this->is_valid( $rules )) exit;

    	$noti_data = array(
        	"device_id" => $this->post('device_id')
    	);

    	if ( !$this->Noti->exists( $noti_data )) {
    	// if device id is not existed, return success

    		$this->success_response( get_msg( 'success_noti_unregister '));
    	}
    		
    	if ( !$this->Noti->delete_by( $noti_data )) {
    	// if there is an error in deleteing noti data, return error

    		$this->error_response( get_msg( 'err_noti_unregister' ));
    	}

    	// if no error, return success
    	$this->success_response( get_msg( 'success_noti_unregister '));
	}
}