<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * Notis Controller
 */
class Notis extends BE_Controller {

	/**
	 * Construt required variables
	 */
	function __construct() {

		parent::__construct( MODULE_CONTROL, 'NOTIS' );
	}

	/**
	* Load Notification Sending Form
	*/
	function index() {
		$this->data['action_title'] = "Push Notification";
		$this->load_form($this->data);
	}

	/**
	* Sending Push Notification Message
	*/
	function push_message() {
		
		if ( $this->input->server( 'REQUEST_METHOD' ) == "POST" ) {
				
			$message = htmlspecialchars_decode($this->input->post( 'message' ));

			$error_msg = "";
			$success_device_log = "";

			// Android Push Notification
			$devices = $this->Noti->get_all()->result();

			$device_ids = array();
			if ( count( $devices ) > 0 ) {
				foreach ( $devices as $device ) {
					$device_ids[] = $device->device_id;
				}
			}

			$status = $this->send_android_fcm( $device_ids, array( "message" => $message ));
			if ( !$status ) $error_msg .= "Fail to push all android devices <br/>";

			
			// response message
			if ( $status ) {
				$this->session->set_flashdata( 'success', "Successfully Sent Push Notification.<br>" . $success_device_log );
			}

			if ( !empty( $error_msg )) {
				$this->session->set_flashdata( 'error', $error_msg );
			}
			
			$this->module_site_url('push_message');
			
		}

		$this->data['action_title'] = "Push Notification";
		$this->load_form($this->data);
	}

	/**
	* Sending Message From FCM For Android
	*/
	function send_android_fcm( $registatoin_ids, $message) 
    {
    	//Google cloud messaging GCM-API url
    	$url = 'https://fcm.googleapis.com/fcm/send';
    	$fields = array(
    	    'registration_ids' => $registatoin_ids,
    	    'data' => $message,
    	);
    	// Update your Google Cloud Messaging API Key
    	//define("GOOGLE_API_KEY", "AIzaSyCCwa8O4IeMG-r_M9EJI_ZqyybIawbufgg");
    	define("GOOGLE_API_KEY", $this->config->item( 'fcm_api_key' ));  	
    		
    	$headers = array(
    	    'Authorization: key=' . GOOGLE_API_KEY,
    	    'Content-Type: application/json'
    	);
    	$ch = curl_init();
    	curl_setopt($ch, CURLOPT_URL, $url);
    	curl_setopt($ch, CURLOPT_POST, true);
    	curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    	curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);	
    	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    	curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
    	$result = curl_exec($ch);				
    	if ($result === FALSE) {
    	    die('Curl failed: ' . curl_error($ch));
    	}
    	curl_close($ch);
    	
    	return $result;
    }


    

}