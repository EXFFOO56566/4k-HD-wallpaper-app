<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * PanaceaSoft Authentication
 */
class PS_Adapter {

	// codeigniter instance
	protected $CI;

	// login user
	protected $login_user_id;

	/**
	 * Constructor
	 */
	function __construct()
	{
		// get CI instance
		$this->CI =& get_instance();
	}

	/**
	 * Sets the login user.
	 */
	function set_login_user_id( $user_id )
	{
		$this->login_user_id = $user_id;
	}
	
	/**
	 * Gets the default photo.
	 *
	 * @param      <type>  $id     The identifier
	 * @param      <type>  $type   The type
	 */
	function get_default_photo_for_wp( $id, $type, $is_gif )
	{
		$default_photo = "";

		// get all images
		$img = $this->CI->Image->get_all_by( array( 'img_parent_id' => $id, 'img_type' => $type ))->result();

		if ( count( $img ) > 0 ) {
		// if there are images for wallpaper,
			
			$default_photo = $img[0];


		    if($is_gif == 1){

		    	$image = $img[0]->img_path;

		    	$image = explode('.', $image);

				$newpng = $image[0];

				$newpng = "$newpng.png";


		    	$default_photo->image_path_thumb = $newpng;
		    } else {
		    	$default_photo->image_path_thumb = $img[0]->img_path;

		    }
		    
			
			$image = get_headers(base_url() . "/uploads/" . $img[0]->img_path, 1);
		    $size = $image["Content-Length"];
		   
		    $unit = ['Byte','KB','MB','GB','TB','PB','EB','ZB','YB'];

		    for($i = 0; $size >= 1024 && $i < count($unit)-1; $i++){
		        $size /= 1024;
		    }

		    $default_photo->size = round($size, 2).' '.$unit[$i];
	


		} else {
		// if no image, return empty object

			$default_photo = $this->CI->Image->get_empty_object();
		}

		return $default_photo;
		 //print_r($default_photo);die;
	}

	function get_default_photo( $id, $type )
	{
		$default_photo = "";

		// get all images
		$img = $this->CI->Image->get_all_by( array( 'img_parent_id' => $id, 'img_type' => $type ))->result();

		if ( count( $img ) > 0 ) {
		// if there are images for wallpaper,
			
			$default_photo = $img[0];
			
			$image = get_headers(base_url() . "/uploads/" . $img[0]->img_path, 1);
		    $size = $image["Content-Length"];
		   
		    $unit = ['Byte','KB','MB','GB','TB','PB','EB','ZB','YB'];

		    for($i = 0; $size >= 1024 && $i < count($unit)-1; $i++){
		        $size /= 1024;
		    }

		    $default_photo->size = round($size, 2).' '.$unit[$i];


		} else {
		// if no image, return empty object

			$default_photo = $this->CI->Image->get_empty_object();
		}

		return $default_photo;
	}

	/**
	 * Customize wallpaper object
	 *
	 * @param      <type>  $obj    The object
	 */
	function convert_image( &$obj )
	{

	}

	/**
	 * Customize wallpaper object
	 *
	 * @param      <type>  $obj    The object
	 */
	function convert_wallpaper( &$obj )
	{

		if ($obj->is_video_wallpaper == 1) {
			// set default video icon
			$obj->default_photo = $this->get_default_photo_for_wp( $obj->wallpaper_id, 'video-icon', $obj->is_gif );
		} else {
			// set default photo
			$obj->default_photo = $this->get_default_photo_for_wp( $obj->wallpaper_id, 'wallpaper', $obj->is_gif );
		}

		// set default video
		$obj->default_video = $this->get_default_video( $obj->wallpaper_id, 'video' );

		// category object
		if ( isset( $obj->cat_id )) {
			$tmp_category = $this->CI->Category->get_one( $obj->cat_id );

			$this->convert_category( $tmp_category );

			$obj->category = $tmp_category;
		}

		// color object
		if ( isset( $obj->color_id )) {
			$tmp_color = $this->CI->Color->get_one( $obj->color_id );
			$obj->color = $tmp_color;
		}

		// user object
		if ( isset( $obj->added_user_id )) {
			$tmp_user = $this->CI->User->get_one( $obj->added_user_id );
			$obj->user = $tmp_user;
		}

		//Need to check for Favourite
		$obj->is_favourited = 0;

		$obj->is_buy = 0;

		//print_r($obj);die;
		//print_r($obj->login_user_id_post);die;
		//print_r($this->get_login_user_id());die;

		if($this->get_login_user_id() != "") {
			//Need to check for Fav
			$conds['wallpaper_id'] = $obj->wallpaper_id;
			$conds['user_id']    = $this->get_login_user_id();

			$fav_id = $this->CI->Favourite->get_one_by($conds)->fav_id;
			$obj->is_favourited = 0;

			if($fav_id != "") {

				$obj->is_favourited = 1;
			} else {
				$obj->is_favourited = 0;
			}

			//For Earning Point 
			$conds_earning['wallpaper_id'] = $obj->wallpaper_id;
			$conds_earning['user_id']      = $this->get_login_user_id();

			$buy_id = $this->CI->Earninghistory->get_one_by($conds_earning)->id;
			

			if($buy_id != "") {
				$obj->is_buy = 1;
			} else {
				$obj->is_buy = 0;
			}



		} else if($obj->login_user_id_post != "") {
			$conds['wallpaper_id'] = $obj->wallpaper_id;
			$conds['user_id']    = $obj->login_user_id_post;

			$fav_id = $this->CI->Favourite->get_one_by($conds)->fav_id;
			// $obj->is_favourited = 0;
			if($fav_id != "") {
				$obj->is_favourited = 1;
			} else {

				$obj->is_favourited = 0;
			}

			//For Earning Point 
			$conds_earning['wallpaper_id'] = $obj->wallpaper_id;
			$conds_earning['user_id']      = $obj->login_user_id_post;

			$buy_id = $this->CI->Earninghistory->get_one_by($conds_earning)->id;
			
			if($buy_id != "") {
				$obj->is_buy = 1;
			} else {
				$obj->is_buy = 0;
			}

		}

		//unset($obj->login_user_id_post);

		
		$obj->rating_count = $obj->overall_rating;

		
	}

	
	/**
	 * Customize category object
	 *
	 * @param      <type>  $obj    The object
	 */
	function convert_category( &$obj )
	{
		//image count
		$obj->image_count = $this->CI->Wallpaper->count_all_by(array("cat_id" => $obj->cat_id));
		// set default photo
		$obj->default_photo = $this->get_default_photo( $obj->cat_id, 'category' );
	}

	/**
	 * Sets the login user.
	 */
	function get_login_user_id()
	{
		return $this->login_user_id;
	}


	


	/**
	 * Customize about object
	 *
	 * @param      <type>  $obj    The object
	 */
	function convert_about( &$obj )
	{
		// set default photo
		$obj->default_photo = $this->get_default_photo( $obj->about_id, 'about' );

	}

	/**
	 * Customize about object
	 *
	 * @param      <type>  $obj    The object
	 */
	function convert_user( &$obj )
	{
		

	}

	/**
	 * Gets the default video.
	 *
	 * @param      <type>  $id     The identifier
	 * @param      <type>  $type   The type
	 */
	function get_default_video( $id, $type )
	{
		$default_video = "";

		// get all video
		$video = $this->CI->Image->get_all_by( array( 'img_parent_id' => $id, 'img_type' => $type ))->result();

		if ( count( $video ) > 0 ) {
		// if there are videos for news,
			
			$default_video = $video[0];
		} else {
		// if no image, return empty object

			$default_video = $this->CI->Image->get_empty_object();
		}

		return $default_video;
	}
	

}