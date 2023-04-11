<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * Model class for touch table
 */
class Favourite extends PS_Model {

	/**
	 * Constructs the required data
	 */
	function __construct() 
	{
		parent::__construct( 'psw_favourite', 'fav_id', 'fav' );
	}

	/**
	 * Implement the where clause
	 *
	 * @param      array  $conds  The conds
	 */
	function custom_conds( $conds = array())
	{
		// touch_id condition
		if ( isset( $conds['fav_id'] )) {
			$this->db->where( 'fav_id', $conds['fav_id'] );
		}

		// wallpaper_id condition
		if ( isset( $conds['wallpaper_id'] )) {
			//echo "kkkk";
			$this->db->where( 'wallpaper_id', $conds['wallpaper_id'] );
		}

		// wallpaper_id condition
		if ( isset( $conds['user_id'] )) {
			$this->db->where( 'user_id', $conds['user_id'] );
		}
	}
}