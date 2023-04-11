<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * Likes Controller
 */

class Shuffles extends BE_Controller {
		/**
	 * Construt required variables
	 */
	function __construct() {

		parent::__construct( MODULE_CONTROL, 'SHUFFLE' );
	}

	/**
	 * Load Shuffle Key Entry Form
	 */

	function index( $id = "shuffle1" ) {

		if ( $this->is_POST()) {
		// if the method is post

			// server side validation
			if ( $this->is_valid_input()) {
				// save user info
				$this->save( $id );
			}
		}

		//Get shuffle Object
		$this->data['shuffle'] = $this->Shuffle->get_one( $id );

		$this->load_form($this->data);

	}

	/**
	 * Update the existing one
	 */
	function edit( $id = "shuffle1") {


		// load user
		$this->data['shuffle'] = $this->Shuffle->get_one( $id );

		// call the parent edit logic
		parent::edit( $id );
	}

	/**
	 * Saving Logic
	 * 1) save about data
	 * 2) check transaction status
	 *
	 * @param      boolean  $id  The about identifier
	 */
	function save( $id = false ) {

		// start the transaction
		$this->db->trans_start();
		
		// prepare data for save
		$data = array();

		// id
		if ( $this->has_data( 'id' )) {
			$data['id'] = $this->get_data( 'id' );
		}

		// if 'daily' is checked,	
		if ($this->input->post('status') == 'daily') {
			$data['status'] = 'daily';
		}

		// if 'monthly' is checked,	
		if ($this->input->post('status') == 'monthly') {
			$data['status'] = 'monthly';
		}

		// if 'yearly' is checked,	
		if ($this->input->post('status') == 'yearly') {
			$data['status'] = 'yearly';
		}

		// if 'manaul' is checked,	
		if ($this->input->post('status') == 'manaul') {
			$data['status'] = 'manaul';
		}

		// if 'no' is checked,	
		if ($this->input->post('status') == 'no') {
			$data['status'] = 'no';
		}

		
		// save shuffle
		if ( ! $this->Shuffle->save( $data, $id )) {
		// if there is an error in inserting user data,	

			// rollback the transaction
			$this->db->trans_rollback();

			// set error message
			$this->data['error'] = get_msg( 'err_model' );
			
			return;
		}


		// commit the transaction
		if ( ! $this->check_trans()) {
        	
			// set flash error message
			$this->set_flash_msg( 'error', get_msg( 'err_model' ));
		} else {

			if ( $id ) {
			// if user id is not false, show success_add message
				
				$this->set_flash_msg( 'success', get_msg( 'success_shuffle_edit' ));
			} else {
			// if user id is false, show success_edit message

				$this->set_flash_msg( 'success', get_msg( 'success_shuffle_add' ));
			}
		}
		redirect( $this->module_site_url() );
	}

	/**
	 * Determines if valid input.
	 *
	 * @return     boolean  True if valid input, False otherwise.
	 */
	function is_valid_input( $id = 0 ) {
		
		return true;
	}
	
}