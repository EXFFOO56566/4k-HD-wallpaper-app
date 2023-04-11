<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * Model class for touch table
 */
class Wallpaper_delete extends PS_Model {

	/**
	 * Constructs the required data
	 */
	function __construct() 
	{
		parent::__construct( 'psw_wallpapers_deleted', 'id', 'del' );
	}

	/**
	 * Implement the where clause
	 *
	 * @param      array  $conds  The conds
	 */
	function custom_conds( $conds = array())
	{
		

		// wallpaper_id condition
		if ( isset( $conds['wallpaper_id'] )) {
			$this->db->where( 'wallpaper_id', $conds['wallpaper_id'] );
		}

		//Date Range
		if ( isset( $conds['start_date'] ) && isset( $conds['end_date'] )) {
			$this->db->where( 'deleted_date >=', $conds['start_date'] );
			$this->db->where( 'deleted_date <=', $conds['end_date'] );
		}


	}
}