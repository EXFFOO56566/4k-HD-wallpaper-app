<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * Model class for about table
 */
class Noti extends PS_Model {

	/**
	 * Constructs the required data
	 */
	function __construct() 
	{
		parent::__construct( 'psw_push_notification_tokens', 'push_noti_token_id', 'noti' );
	}

	/**
	 * Implement the where clause
	 *
	 * @param      array  $conds  The conds
	 */
	function custom_conds( $conds = array())
	{
		// push_noti_token_id condition
		if ( isset( $conds['push_noti_token_id'] )) {
			$this->db->where( 'push_noti_token_id', $conds['push_noti_token_id'] );
		}

		// os_type condition
		if ( isset( $conds['os_type'] )) {
			$this->db->where( 'os_type', $conds['os_type'] );
		}

		// os_type condition
		if ( isset( $conds['device_id'] )) {
			$this->db->where( 'device_id', $conds['device_id'] );
		}

		
	}
}