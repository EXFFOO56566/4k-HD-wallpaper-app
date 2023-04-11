<?php
require_once( APPPATH .'libraries/REST_Controller.php' );

/**
 * REST API for News
 */
class Wallpapers extends API_Controller
{

	/**
	 * Constructs Parent Constructor
	 */
	function __construct()
	{
		parent::__construct( 'Wallpaper' );
	}

	/**
	 * Default Query for API
	 * @return [type] [description]
	 */
	function default_conds()
	{
		$conds = array();


		if ( $this->is_get ) {
		// if is get record using GET method, get default setting for GET_ALL_CATEGORIES
			
			$setting = $this->Api->get_one_by( array( 'api_constant' => GET_ALL_WALLPAPERS ));
		}

		if ( $this->is_search ) {
		// if is get record using POST method, get default setting for SEARCH_WALLPAPERS
		
			$setting = $this->Api->get_one_by( array( 'api_constant' => SEARCH_WALLPAPERS ));
			
				$conds['searchterm']     	 = $this->post('wallpaper_name');
				$conds['cat_id'] 		 	 = $this->post('cat_id');
				$conds['types']      	 	 = $this->post('type');
				$conds['is_recommended']	 = $this->post('is_recommended');
				$conds['is_portrait']    	 = $this->post('is_portrait');
				$conds['is_landscape']   	 = $this->post('is_landscape');
				$conds['is_square']      	 = $this->post('is_square');
				$conds['color_id']       	 = $this->post('color_id');
				$conds['is_gif'] 		 	 = $this->post('is_gif');
				$conds['is_wallpaper'] 	 	 = $this->post('is_wallpaper');
				$conds['is_video_wallpaper'] = $this->post('is_video_wallpaper');			
						

				if($this->post('is_gif') == "only") {

					$conds['is_gif'] = 1;

				} else if( $this->post('is_gif') == "no" ) {

					$conds['is_gif'] = -1;

				} else if( $this->post('is_gif') == "all" ) {

					$conds['is_gif'] = "";

				}

				if($this->post('added_user_id') != "") {
					$conds['added_user_id']   = $this->post('added_user_id');
				} 
				
				if($this->post('rating_min') != "") {
					$conds['rating_min']   = $this->post('rating_min');
				} 

				if($this->post('rating_max') != "") {
					$conds['rating_max']   = $this->post('rating_max');
				}

				if($this->post('point_min') != "") {
					$conds['point_min']   = $this->post('point_min');
				} 

				if($this->post('point_max') != "") {
					$conds['point_max']   = $this->post('point_max');
				}

				if($this->post('rating_count') != "") {
					$conds['overall_rating']   = $this->post('rating_count');
				}

				$conds['order_by']       = $this->post('order_by');
				$conds['order_type']     = $this->post('order_type');
				
				$conds['only_publish_filter'] = 1;

		}
		
		if ( !empty( $setting )) {
			// if setting is not empty, filter
			if($conds['order_by'] == "") {
				//default read from DB setting

				$conds['order_by'] = 1;
				$conds['order_by_field'] = $setting->order_by_field;
				$conds['order_by_type'] = $setting->order_by_type;	

			} else {

				

				if($conds['order_by'] == "touch_count") {
					
					$conds['order_by_field'] = "touch_count";
					$conds['order_by_type'] = $conds['order_type'];

				} else if($conds['order_by'] == "added_date") {
					
					$conds['order_by_field'] = "added_date";
					$conds['order_by_type'] = $conds['order_type'];

				} else if($conds['order_by'] == "rating_count") {
					$conds['order_by_field'] = "overall_rating";
					$conds['order_by_type'] = $conds['order_type'];

				} else if($conds['order_by'] == "download_count") {

					$conds['order_by_field'] = "download_count";
					$conds['order_by_type'] = $conds['order_type'];

				} else if($conds['order_by'] == "atoz") {

					$conds['order_by_field'] = "wallpaper_name";
					$conds['order_by_type'] = "asc";


				} else if($conds['order_by'] == "ztoa") {

					$conds['order_by_field'] = "wallpaper_name";
					$conds['order_by_type'] = "desc";

				} else if($conds['order_by'] == "point") {

					$conds['order_by_field'] = "point";
					$conds['order_by_type'] = $conds['order_type'];

				} else if($conds['order_by'] == "recommended_date") {

					$conds['order_by_field'] = "recommended_date";
					$conds['order_by_type'] = $conds['order_type'];

				}

			}
			
					
		}

		return $conds;
	}

	
	/**
	 * Convert Object
	 */
	function convert_object( &$obj )
	{
		// call parent convert object
		parent::convert_object( $obj );

		// convert customize category object
		$this->ps_adapter->convert_wallpaper( $obj );
	}

	

}