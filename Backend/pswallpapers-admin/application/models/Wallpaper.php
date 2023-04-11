<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * Model class for wallpaper table
 */
class Wallpaper extends PS_Model {

	/**
	 * Constructs the required data
	 */
	function __construct() 
	{
		parent::__construct( 'psw_wallpapers', 'wallpaper_id', 'paper' );
	}

	/**
	 * Implement the where clause
	 *
	 * @param      array  $conds  The conds
	 */
	function custom_conds( $conds = array())
	{		

		// default where clause
		if ( !isset( $conds['no_publish_unpublish_filter'] )) {
			//echo "asdadad"; die;
			if($conds['no_publish_unpublish_filter'] == 1){
				$this->db->where( 'wallpaper_is_published', 1 );
			}
		}

		if ( isset( $conds['no_publish_unpublish_filter_for_users'] )) {
			if($conds['no_publish_unpublish_filter_for_users'] == 1) {
				$this->db->group_start();
				$this->db->where( 'wallpaper_is_published', 0 );
				$this->db->or_where( 'wallpaper_is_published', 1 );
				$this->db->or_where( 'wallpaper_is_published', 2 );
				$this->db->or_where( 'wallpaper_is_published', 3 );
				$this->db->group_end();
			}
		}

		// order by
		if ( isset( $conds['order_by'] )) {
			
			$order_by_field = $conds['order_by_field'];
			$order_by_type = $conds['order_by_type'];
			
			$this->db->order_by( 'psw_wallpapers.'.$order_by_field, $order_by_type);
		}

		// popular conditions
		if ( $this->is_filter_popular( $conds )) {

			$this->db->reset_query();

			$n = $this->table_name;
			$t = 'psw_touches';

			$this->db->select( "{$n}.*, count({$t}.user_id) touch_count" );

			// join user_categories table by wallpaper_id
			$this->db->join( $t, $t .'.wallpaper_id = '. $this->table_name .'.wallpaper_id');

			// group by news id
			$this->db->group_by( $n .'.wallpaper_id' );

			// condition for user_categories table
			$this->db->order_by( "touch_count", "desc" );
		}

		// wallpaper_name condition
		if ( isset( $conds['wallpaper_name'] )) {
			$this->db->where( 'wallpaper_name', $conds['wallpaper_name'] );
		}

		// added_user_id condition
		if ( isset( $conds['added_user_id'] )) {
			$this->db->where( 'added_user_id', $conds['added_user_id'] );
		}

		// category id condition
		if ( isset( $conds['cat_id'] )) {
			
			if ($conds['cat_id'] != "") {
				if($conds['cat_id'] != '0'){
				
					$this->db->where( 'cat_id', $conds['cat_id'] );	
				}

			}			
		}

		// color id condition
		if ( isset( $conds['color_id'] )) {
			
			if ($conds['color_id'] != "") {
				if($conds['color_id'] != '0') {
				
					$this->db->where( 'color_id', $conds['color_id'] );	
				}

			}			
		}

		// wallpaper_id condition
		if ( isset( $conds['wallpaper_id'] )) {
			
			if ($conds['wallpaper_id'] != "") {
				if($conds['wallpaper_id'] != '0') {
				
					$this->db->where( 'wallpaper_id', $conds['wallpaper_id'] );	
				}

			}			
		}

		// color id condition
		if ( isset( $conds['added_user_id'] )) {
			
			if ($conds['added_user_id'] != "") {
				if($conds['added_user_id'] != '0') {
					$this->db->where( 'added_user_id', $conds['added_user_id'] );	
				}

			}			
		}

		// type condition
		if ( isset( $conds['types'] )) {
			
			if ($conds['types'] != "") {
				if($conds['types'] != '0') {
				
					$this->db->where( 'types', $conds['types'] );	
				}

			}			
		}

		// point condition
		if ( isset( $conds['point'] )) {
			
			if ($conds['point'] != "") {
				if($conds['point'] != '0') {
				
					$this->db->where( 'point', $conds['point'] );	
				}

			}			
		}

		// searchterm
		if ( isset( $conds['searchterm'] )) {
			$this->db->like( 'wallpaper_name', $conds['searchterm'] );
		}
	
		// is_recommended condition
		if ( isset( $conds['is_recommended'] )) {
			$this->db->where( 'is_recommended', $conds['is_recommended'] );
		}


		// is_gif condition
		if ( isset( $conds['is_gif'] )) {
			
			if( $conds['is_gif'] == -1 ) {

				$this->db->where( 'is_gif !=', 1 );

			} else {

				$this->db->where( 'is_gif', $conds['is_gif'] );
			
			} 

			
		}

		// is_wallpaper condition
		if ( isset( $conds['is_wallpaper'] )) {
			
			if( $conds['is_wallpaper'] == -1 ) {

				$this->db->where( 'is_wallpaper !=', 1 );

			} else {

				$this->db->where( 'is_wallpaper', $conds['is_wallpaper'] );
			
			} 

			
		}

		// is_video_wallpaper condition
		if ( isset( $conds['is_video_wallpaper'] )) {
			
			if( $conds['is_video_wallpaper'] == -1 ) {

				$this->db->where( 'is_video_wallpaper !=', 1 );

			} else {

				$this->db->where( 'is_video_wallpaper', $conds['is_video_wallpaper'] );
			
			} 

			
		}


		if ( isset( $conds['is_portrait'] ) || isset( $conds['is_landscape'] ) || isset( $conds['is_square'] )) {
			if($conds['is_portrait'] != "" || $conds['is_landscape'] != "" || $conds['is_square']) {
				
				$this->db->group_start();
				$this->db->or_where( 'is_portrait', $conds['is_portrait'] );
				$this->db->or_where( 'is_landscape', $conds['is_landscape'] );
				$this->db->or_where( 'is_square', $conds['is_square'] );
				$this->db->group_end();

			}
		}



		// point condition
		if ( isset( $conds['point_min'] ) ) {
			$this->db->where( 'point >= ', $conds['point_min'] );
		}

		if ( isset( $conds['point_max'] ) ) {
			$this->db->where( 'point <= ', $conds['point_max'] );
		}

		// point condition
		if ( isset( $conds['rating_min'] ) ) {
			$this->db->where( 'overall_rating >= ', $conds['rating_min'] );
		}

		if ( isset( $conds['rating_max'] ) ) {
			$this->db->where( 'overall_rating <= ', $conds['rating_max'] );
		}

		$this->db->order_by("added_date", "DESC");
		$this->db->order_by("wallpaper_name", "ASC");
	}

	/**
	 * Determines if filter popular.
	 *
	 * @return     boolean  True if filter popular, False otherwise.
	 */
	function is_filter_popular( $conds )
	{
		return ( isset( $conds['popular'] ) && $conds['popular'] == 1 );
	}
}