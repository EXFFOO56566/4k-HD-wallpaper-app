<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * PanaceaSoft Database Trigger
 */
class PS_Delete {

	// codeigniter instance
	protected $CI;

	/**
	 * Constructor
	 */
	function __construct()
	{
		// get CI instance
		$this->CI =& get_instance();

		// load image library
		$this->CI->load->library( 'PS_Image' );
	}

	/**
	 * Delete the category and image under the category
	 *
	 * @param      <type>  $id     The identifier
	 */
	function delete_category( $category_id, $enable_trigger = false )
	{		
		if ( ! $this->CI->Category->delete( $category_id )) {
		// if there is an error in deleting category,
			
			return false;
		}

		// prepare condition
		$conds = array( 'img_type' => 'category', 'img_parent_id' => $category_id );

		if ( $this->CI->delete_images_by( $conds )) {
			$conds = array( 'img_type' => 'category-icon', 'img_parent_id' => $category_id );

			if ( !$this->CI->delete_images_by( $conds )) {
			// if error in deleting image, 

				return false;
			}
		}

		if ( $enable_trigger ) {
		// if execute_trigger is enable, trigger to delete wallpaper related data
			if ( ! $this->delete_category_trigger( $category_id )) {
			// if error in deleteing wallpaper and wallpaper related data

				return false;
			}
		}

		return true;
	}

	

	/**
	 * Delete the category and image under the category
	 *
	 * @param      <type>  $id     The identifier
	 */
	function delete_color( $color_id, $enable_trigger = false )
	{		
		if ( ! $this->CI->Color->delete( $color_id )) {
		// if there is an error in deleting category,
			
			return false;
		}

		

		if ( $enable_trigger ) {
		// if execute_trigger is enable, trigger to delete wallpaper related data

			if ( ! $this->delete_color_trigger( $color_id )) {
			// if error in deleteing wallpaper and wallpaper related data

				return false;
			}
		}

		return true;
	}

	/**
	 * Delete the category and image under the category
	 *
	 * @param      <type>  $id     The identifier
	 */
	function delete_user( $user_id )
	{		
		if ( ! $this->CI->User->delete( $user_id )) {
		// if there is an error in deleting category,
			
			return false;
		}


		return true;
	}

	/**
	 * Delete the wallpaper and image under the wallpaper
	 *
	 * @param      <type>  $id     The identifier
	 */
	function delete_wallpaper( $wallpaper_id, $enable_trigger = false )
	{
		if ( ! $this->CI->Wallpaper->delete( $wallpaper_id )) {
		// if there is an error in deleting wallpaper,
			
			return false;
		}  
		
		// prepare condition
		$conds = array( 'img_type' => 'wallpaper', 'img_parent_id' => $wallpaper_id );

		if ( !$this->CI->delete_images_by( $conds )) {
		// if error in deleting image, 

			return false;
		}
	

		if ( $enable_trigger ) {
		// if execute_trigger is enable, trigger to delete wallpaper related data

			if ( !$this->delete_wallpaper_trigger( $wallpaper_id )) {
			// if error in deleting wallpaper related data,

				return false;
			}
			
		}

		return true;
	}

	/**
	 * Trigger to delete wallpaper and related data when category is deleted
	 * delete wallpaper
	 * delete wallpaper images
	 * call delete_wallpaper_trigger
	 */
	function delete_category_trigger( $cat_id )
	{
		// get all wallpaper and delete the wallpaper under the category
		$wallpapers = $this->CI->Wallpaper->get_all_by( array( 'cat_id' => $cat_id, 'no_publish_filter' => 1 ))->result();

		if ( !empty( $wallpapers )) {
		// if the wallpaper list not empty
			
			// loop all the wallpaper
			foreach ( $wallpapers as $wallpaper ) {

				// delete wallpaper and images
				$enable_trigger = true;

				if ( !$this->delete_wallpaper( $wallpaper->wallpaper_id, $enable_trigger )) {
				// if error in deleting wallpaper,

					return false;
				} 

			}
		}

		return true;
	}


	/**
	 * Trigger to unlink wallpaper and related data when color is deleted
	 * remove color_id from wallpaper
	 */
	function delete_color_trigger( $color_id )
	{
		
		$conds['color_id'] = $color_id;
		$wallpapers = $this->CI->Wallpaper->get_all_by($conds)->result();
		foreach ( $wallpapers as $wallpaper ) {
			$data['color_id'] = "";
			$this->CI->Wallpaper->save($data, $wallpaper->wallpaper_id);
		}
		return true;
		
		
	}

	/**
	* Trigger to delete wallpaper related data when wallpaper is deleted
	* delete wallpaper related data
	*/
	function delete_wallpaper_trigger( $wallpaper_id )
	{
		$conds = array( 'wallpaper_id' => $wallpaper_id );

		// delete touches
		if ( !$this->CI->Rate->delete_by( $conds )) {

			return false;
		}

		if ( !$this->CI->Touch->delete_by( $conds )) {

			return false;
		}

		if ( !$this->CI->Favourite->delete_by( $conds )) {

			return false;
		}

		if ( !$this->CI->Earningpoint->delete_by( $conds )) {

			return false;
		}

		if ( !$this->CI->Download->delete_by( $conds )) {

			return false;
		}

		return true;
	}

	/**
	 * Delete history for API
	 *
	 * @param      <type>  $id     The identifier
	 */
	function delete_history( $type_id, $type_name, $enable_trigger = false )
	{		
		if( $type_name == "wallpaper") {


			if ( ! $this->CI->Wallpaper->delete( $type_id )) {
			// if there is an error in deleting product,
				
				return false;
			} else {
				//product is successfully deleted so need to save in log table
				$data_delete['type_id']   = $type_id;
				$data_delete['type_name'] = $type_name;

				$this->CI->Delete_history->save($data_delete);
			}
			$conds = array( 'img_type' => 'wallpaper', 'img_parent_id' => $type_id );
			$this->CI->delete_images_by( $conds );

			$conds_video = array( 'img_type' => 'video', 'img_parent_id' => $type_id );
			$this->CI->delete_images_by( $conds_video );

			$conds_icon = array( 'img_type' => 'video-icon', 'img_parent_id' => $type_id );
			$this->CI->delete_images_by( $conds_icon );

		} else if ( $type_name == "category" ) {


			if ( ! $this->CI->Category->delete( $type_id )) {
			// if there is an error in deleting product,
				
				return false;
			} else {
				//product is successfully deleted so need to save in log table
				$data_delete['type_id']   = $type_id;
				$data_delete['type_name'] = $type_name;


				//$this->CI->Product_delete->save($data_delete);
				$this->CI->Delete_history->save($data_delete);
			}
			$conds = array( 'img_type' => 'category', 'img_parent_id' => $type_id );
			$this->CI->delete_images_by( $conds );

		}
		
		if ( $enable_trigger ) {
		// if execute_trigger is enable, trigger to delete wallpaper related data
			if( $type_name == "wallpaper" ) {
				if ( !$this->delete_wallpaper_trigger( $type_id )) {
				// if error in deleting wallpaper related data,

					return false;
				}
			} else if( $type_name == "category" ) {

				if ( !$this->delete_category_trigger( $type_id )) {
				// if error in deleting wallpaper related data,
					return false;
				}

			} 
			
		}

		return true;
	}

	/**
	 * Delete Image by id and type
	 *
	 * @param      <type>  $conds  The conds
	 */
	function delete_images_by( $conds )
	{
		// get all images
		$images = $this->CI->Image->get_all_by( $conds );

		if ( !empty( $images )) {
		// if images are not empty,

			foreach ( $images->result() as $img ) {
			// loop and delete each image

				if ( ! $this->CI->ps_image->delete_images( $img->img_path ) ) {
				// if there is an error in deleting images

					return false;
				}
			}
		}

		if ( ! $this->CI->Image->delete_by( $conds )) {
		// if error in deleting from database,

			return false;
		}

		return true;
	}
}