<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * Empty Class
 */
class My_Model {}

/**
 * PanaceaSoft Base Model
 */
class PS_Model extends CI_Model {
	
	// name of the database table
	protected $table_name;

	// name of the ID field
	public $primary_key;

	// name of the key prefix
	protected $key_prefix;

	/**
	 * constructs required data
	 */
	function __construct( $table_name, $primary_key = false, $key_prefix = false )
	{
		parent::__construct();

		// set the table name
		$this->table_name = $table_name;
		$this->primary_key = $primary_key;
		$this->key_prefix = $key_prefix;
	}

	/**
	 * Empty class to be extended
	 *
	 * @param      array  $conds  The conds
	 */
	function custom_conds( $conds = array()) {

	}

	/**
	 * Generate the TeamPS Unique Key
	 *
	 * @return     <type>  ( description_of_the_return_value )
	 */
	function generate_key()
	{
		return $this->key_prefix . md5( $this->key_prefix . microtime() . uniqid() . 'teamps' );
	}

    /**
     * Determines if exist.
     *
     * @param      <type>   $id     The identifier
     *
     * @return     boolean  True if exist, False otherwise.
     */
    function is_exist( $id ) {
    	
    	// from table
    	$this->db->from( $this->table_name );

    	// where clause
		$this->db->where( $this->primary_key, $id );
		
		// get query
		$query = $this->db->get();

		// return the result
		return ($query->num_rows()==1);
    }

    /**
     * Save the data if id is not existed
     *
     * @param      <type>   $data   The data
     * @param      boolean  $id     The identifier
     */
	function save( &$data, $id = false ) {
	
		if ( !$id ) {
		
		// if id is not false and id is not yet existed,
			if ( !empty( $this->primary_key ) && !empty( $this->key_prefix )) {
			// if the primary key and key prefix is existed,
			
				// generate the unique key
				$data[ $this->primary_key ] = $this->generate_key();
			}

			// insert the data as new record
			 return $this->db->insert( $this->table_name, $data );

			 //print_r($this->db->last_query());die;
		} else {
			
		
		// else
			// where clause
			$this->db->where( $this->primary_key, $id);

			// update the data
			return $this->db->update($this->table_name,$data);
		}
	}

	/**
	 * Returns all the records
	 *
	 * @param      boolean  $limit   The limit
	 * @param      boolean  $offset  The offset
	 */
	function get_all( $limit = false, $offset = false ) {

		// where clause
		$this->custom_conds();

		// from table
		$this->db->from($this->table_name);


		if ( $limit ) {
		// if there is limit, set the limit
			
			$this->db->limit($limit);
		}
		
		if ( $offset ) {
		// if there is offset, set the offset,
			
			$this->db->offset($offset);
		}
		return $this->db->get();
		//print_r($this->db->last_query());die;
	}

	/**
	 * Returns the total count
	 */
	function count_all() {
		// from table
		$this->db->from( $this->table_name );

		// where clause
		$this->custom_conds();

		// return the count all results
		return $this->db->count_all_results();
		// print_r($this->db->last_query());die;
	}

	/**
	 * Return the info by Id
	 *
	 * @param      <type>  $id     The identifier
	 */
	function get_one( $id ) {
		
		// query the record
		$query = $this->db->get_where( $this->table_name, array( $this->primary_key => $id ));
		
		if ( $query->num_rows() == 1 ) {
		// if there is one row, return the record
			
			return $query->row();
		} else {
		// if there is no row or more than one, return the empty object
			
			return $this->get_empty_object( $this->table_name );
		}
	}

	/**
	 * Returns the multiple Info by Id
	 *
	 * @param      array  $ids    The identifiers
	 */
	function get_multi_info( $ids = array()) {
		
		// from table
		$this->db->from( $this->table_name );

		// where clause
		$this->db->where_in( $this->primary_key, $ids );

		// returns
		return $this->db->get();
	}

	/**
	 * Delete the records by Id
	 *
	 * @param      <type>  $id     The identifier
	 *
	 * @return     <type>  ( description_of_the_return_value )
	 */
	function delete( $id )
	{
		// where clause
		$this->db->where( $this->primary_key, $id );

		// delete the record
		return $this->db->delete( $this->table_name );
 	}

 	/**
 	 * Delete the records by ids
 	 *
 	 * @param      array   $ids    The identifiers
 	 *
 	 * @return     <type>  ( description_of_the_return_value )
 	 */
 	function delete_list( $ids = array()) {
 		
 		// where clause
		$this->db->where_in( $this->primary_key, $id );

		// delete the record
		return $this->db->delete( $this->table_name );
 	}

	/**
	 * returns the object with the properties of the table
	 *
	 * @return     stdClass  The empty object.
	 */
    function get_empty_object()
    {   
        $obj = new stdClass();
        
        $fields = $this->db->list_fields( $this->table_name );
        foreach ( $fields as $field ) {
            $obj->$field = '';
        }
        $obj->is_empty_object = true;
        return $obj;
    }

   	/**
   	 * Execute The query
   	 *
   	 * @param      <type>   $sql     The sql
   	 * @param      <type>   $params  The parameters
   	 *
   	 * @return     boolean  ( description_of_the_return_value )
   	 */
	function exec_sql( $sql, $params = false )
	{
		if ( $params ) {
		// if the parameter is not false

			// bind the parameter and run the query
			return $this->db->query( $sql, $params );	
		}

		// if there is no parameter,
		return $this->db->query( $sql );
	}

	/**
	 * Implement the where clause
	 *
	 * @param      array  $conds  The conds
	 */
	function conditions( $conds = array())
	{
		// if condition is empty, return true
		if ( empty( $conds )) return true;
	}

	/**
	 * Check if the key is existed,
	 *
	 * @param      array   $conds  The conds
	 *
	 * @return     <type>  ( description_of_the_return_value )
	 */
	function exists( $conds = array()) {

		// where clause
		$this->custom_conds( $conds );
		
		// from table
		$this->db->from( $this->table_name );

		// get query
		$query = $this->db->get();

		// return the result
		return ($query->num_rows() == 1);
		// print_r($this->db->last_query());die;
	}

	/**
	 * Gets all by the conditions
	 *
	 * @param      array    $conds   The conds
	 * @param      boolean  $limit   The limit
	 * @param      boolean  $offset  The offset
	 *
	 * @return     <type>   All by.
	 */
	function get_all_by( $conds = array(), $limit = false, $offset = false ) {
		if ( isset( $conds['no_publish_unpublish_filter'] )) {
			$this->db->group_start();
			$this->db->where( 'wallpaper_is_published', 0 );
			$this->db->or_where( 'wallpaper_is_published', 1 );
			$this->db->group_end();
		}

		if ( isset( $conds['no_publish_unpublish_filter_for_users'] )) {
			$this->db->group_start();
			$this->db->where( 'wallpaper_is_published', 0 );
			$this->db->or_where( 'wallpaper_is_published', 1 );
			$this->db->or_where( 'wallpaper_is_published', 2 );
			$this->db->or_where( 'wallpaper_is_published', 3 );
			$this->db->group_end();
		}

		if ( isset( $conds['only_publish_filter'] )) {
			$this->db->where( 'wallpaper_is_published', 1 );
		}


		if(isset($conds['order_by'])) {
			//If sorting from download_count
			if( $conds['order_by_field'] == "download_count" ) {
				if ($conds['order_by'] != "" && $conds['searchterm'] == "" ) {
					// echo "fasd";die;
					$this->db->where( 'download_count !=', 0);	

				} 

				if($conds['order_by'] != "" && $conds['searchterm'] != "") {
					// echo "lll";die;
					$this->db->where( 'download_count', $conds['order_by_type']);	
				}

				if($conds['order_by_type'] == "") {
					$this->db->order_by("download_count", "DESC");
				} else {
					$this->db->order_by("download_count", $conds['order_by_type']);
				}
			}

		}

		if(isset($conds['order_by'])) {

			//If sorting from overall_rating
			if($conds['order_by_field'] == "overall_rating") {

				if ($conds['order_by'] != "" && $conds['searchterm'] == "" ) {
						
					$this->db->where( 'overall_rating !=', 0);	

				}

				if($conds['order_by'] != "" && $conds['searchterm'] != "") {

					$this->db->where( 'overall_rating', $conds['order_by_type']);	
				}

				if($conds['order_by_type'] == "") {
					$this->db->order_by("overall_rating", "DESC");
				} else {
					$this->db->order_by("overall_rating", $conds['order_by_type']);
				}

			}
		}

		if(isset($conds['order_by'])) {

			//If sorting from overall_rating
			if($conds['order_by_field'] == "touch_count") {

				if ($conds['order_by'] != "" || $conds['order_by'] != 0) {
						
					$this->db->where( 'touch_count !=', 0);	

				}

				if($conds['order_by_type'] == "") {
					$this->db->order_by("touch_count", "DESC");
				} else {
					$this->db->order_by("touch_count", $conds['order_by_type']);
				}

			}
		}

		if(isset($conds['order_by'])) {
			//If sorting from overall_rating
			if($conds['order_by_field'] == "added_date") {
				
				if($conds['order_by_type'] == "") {
					$this->db->order_by("added_date", "DESC");
				} else {
					$this->db->order_by("added_date", $conds['order_by_type']);
				}

			}
		}

		if(isset($conds['order_by'])) {
			//If sorting from overall_rating
			if($conds['order_by_field'] == "point") {
				
				if($conds['order_by_type'] == "") {
					$this->db->order_by("point", "DESC");
				} else {
					$this->db->order_by("point", $conds['order_by_type']);
				}

				//If same point then it would be sort by added_date
				$this->db->order_by("added_date", "DESC");
				//echo "dsadasdasdads"; die;
			}
		}



		// where clause
		$this->custom_conds( $conds );

		// from table
		$this->db->from( $this->table_name );

		if ( $limit ) {
		// if there is limit, set the limit
			
			$this->db->limit($limit);
		}
		
		if ( $offset ) {
		// if there is offset, set the offset,
			
			$this->db->offset($offset);
		}
		
	 	return $this->db->get();
		// print_r($this->db->last_query());die;
	
	}

	// get all wallpapaer by app

	function get_all_wallpaper( $conds = array(), $limit = false, $offset = false ) {
		if ( isset( $conds['no_publish_unpublish_filter'] )) {
			$this->db->group_start();
			$this->db->where( 'wallpaper_is_published', 0 );
			$this->db->or_where( 'wallpaper_is_published', 1 );
			$this->db->group_end();
		}

		if ( isset( $conds['no_publish_unpublish_filter_for_users'] )) {
			$this->db->group_start();
			$this->db->where( 'wallpaper_is_published', 0 );
			$this->db->or_where( 'wallpaper_is_published', 1 );
			$this->db->or_where( 'wallpaper_is_published', 2 );
			$this->db->or_where( 'wallpaper_is_published', 3 );
			$this->db->group_end();
		}

		if ( isset( $conds['only_publish_filter'] )) {
			$this->db->where( 'wallpaper_is_published', 1 );
		}


		if(isset($conds['order_by'])) {
			//If sorting from download_count
			if( $conds['order_by_field'] == "download_count" ) {
				if ($conds['order_by'] != "" && $conds['searchterm'] == "" ) {
					// echo "fasd";die;
					$this->db->where( 'download_count !=', 0);	

				} 

				if($conds['order_by'] != "" && $conds['searchterm'] != "") {
					// echo "lll";die;
					$this->db->where( 'download_count', $conds['order_by_type']);	
				}

				if($conds['order_by_type'] == "") {
					$this->db->order_by("download_count", "DESC");
				} else {
					$this->db->order_by("download_count", $conds['order_by_type']);
				}
			}

		}

		if(isset($conds['order_by'])) {

			//If sorting from overall_rating
			if($conds['order_by_field'] == "overall_rating") {

				if ($conds['order_by'] != "" && $conds['searchterm'] == "" ) {
						
					$this->db->where( 'overall_rating !=', 0);	

				}

				if($conds['order_by'] != "" && $conds['searchterm'] != "") {

					$this->db->where( 'overall_rating', $conds['order_by_type']);	
				}

				if($conds['order_by_type'] == "") {
					$this->db->order_by("overall_rating", "DESC");
				} else {
					$this->db->order_by("overall_rating", $conds['order_by_type']);
				}

			}
		}

		if(isset($conds['order_by'])) {

			//If sorting from overall_rating
			if($conds['order_by_field'] == "touch_count") {

				if ($conds['order_by'] != "" || $conds['order_by'] != 0) {
						
					$this->db->where( 'touch_count !=', 0);	

				}

				if($conds['order_by_type'] == "") {
					$this->db->order_by("touch_count", "DESC");
				} else {
					$this->db->order_by("touch_count", $conds['order_by_type']);
				}

			}
		}

		if(isset($conds['order_by'])) {
			//If sorting from overall_rating
			if($conds['order_by_field'] == "added_date") {

				$shuffle = $this->Shuffle->get_all_shuffle()->result();
				//print_r($shuffle[0]->status);die;
					if ($shuffle[0]->status == "no" ) {
						$this->db->order_by("added_date", "DESC");
					} else if ($shuffle[0]->status == "daily" ) {
						$tmp_date = date('Y-m-d');
						$rand_date = explode('-' , $tmp_date);
						$rand_no = $rand_date[2];
						$this->db->order_by("RAND($rand_no)");
					} else if ($shuffle[0]->status == "monthly" ) {
						$tmp_date = date('Y-m-d');
						$rand_date = explode('-' , $tmp_date);
						$rand_no = $rand_date[1] . '' . $rand_date[2];
						$this->db->order_by("RAND($rand_no)");
					} else if ($shuffle[0]->status == "yearly" ) {
						$tmp_date = date('Y-m-d');
						$rand_date = explode('-' , $tmp_date);
						$rand_no = $rand_date[0] . ''. $rand_date[1] . '' . $rand_date[2];
						$this->db->order_by("RAND($rand_no)");
					} else if ($shuffle[0]->status == "manaul" ) {
						$tmp_date = date('Y-m-d-H-i-s');
						$rand_date = explode('-' , $tmp_date);
						$rand_no_one = $rand_date[0] . ''. $rand_date[1] . '' . $rand_date[2];
						$rand_no_two = $rand_date[3] . ''. $rand_date[4] . '' . $rand_date[5];
						$rand_no = $rand_no_one . $rand_no_two;
						$this->db->order_by("RAND($rand_no)");
					}

			}
		}

		if(isset($conds['order_by'])) {
			//If sorting from overall_rating
			if($conds['order_by_field'] == "point") {
				
				if($conds['order_by_type'] == "") {
					$this->db->order_by("point", "DESC");
				} else {
					$this->db->order_by("point", $conds['order_by_type']);
				}

				//If same point then it would be sort by added_date
				$this->db->order_by("added_date", "DESC");
				//echo "dsadasdasdads"; die;
			}
		}



		// where clause
		$this->custom_conds( $conds );

		// from table
		$this->db->from( $this->table_name );

		if ( $limit ) {
		// if there is limit, set the limit
			
			$this->db->limit($limit);
		}
		
		if ( $offset ) {
		// if there is offset, set the offset,
			
			$this->db->offset($offset);
		}
		
	 	return $this->db->get();
		// print_r($this->db->last_query());die;
	
	}

	
	/**
	 * Counts the number of all by the conditions
	 *
	 * @param      array   $conds  The conds
	 *
	 * @return     <type>  Number of all by.
	 */
	function count_all_by( $conds = array()) {

		
		// where clause
		$this->custom_conds( $conds );
		
		// from table
		$this->db->from( $this->table_name );

		// return the count all results
		return $this->db->count_all_results();
		// print_r($this->db->last_query());die;
	}

	/**
	 * Sum the number of all by the conditions
	 *
	 * @param      array   $conds  The conds
	 *
	 * @return     <type>  Number of all by.
	 */
	function sum_all_by( $conds = array()) {
		
		// where clause
		$this->custom_conds( $conds );
		
		$this->db->select_sum('rating');
		// from table
		$this->db->from( $this->table_name );

		// return the count all results
		//return $this->db->count_all_results();
		return $this->db->get();
	}

	/**
	 * Gets the information by.
	 *
	 * @param      array   $conds  The conds
	 *
	 * @return     <type>  The information by.
	 */
	function get_one_by( $conds = array()) {
		// where clause
		$this->custom_conds( $conds );
		// query the record
		$query = $this->db->get( $this->table_name );

		if ( $query->num_rows() == 1 ) {
		// if there is one row, return the record
			
			return $query->row();
		} else {
		// if there is no row or more than one, return the empty object
			
			return $this->get_empty_object( $this->table_name );
		}
	}


	/**
	 * Delete the records by condition
	 *
	 * @param      array   $conds  The conds
	 *
	 * @return     <type>  ( description_of_the_return_value )
	 */
	function delete_by( $conds = array() )
	{
		// where clause
		$this->custom_conds( $conds );

		// delete the record
		return $this->db->delete( $this->table_name );
 	}

 	/**
	 * Delete the records by condition
	 *
	 * @param      array   $conds  The conds
	 *
	 * @return     <type>  ( description_of_the_return_value )
	 */
	function get_wallpaper_count( $conds = array() )
	{
		$this->db->select('psw_wallpapers.*, count(psw_touches.touch_id) as t_count,psw_wallpapers.wallpaper_name'); 
		$this->db->from('psw_touches');
		$this->db->join('psw_wallpapers', 'psw_touches.wallpaper_id = psw_wallpapers.wallpaper_id');
	
		$this->db->limit(5);

		if(isset($conds['wallpaper_id'])) {

			if ($conds['wallpaper_id'] != "" || $conds['wallpaper_id'] != 0) {
					
					$this->db->where( 'wallpaper_id', $conds['wallpaper_id'] );	

			}

		}
		$this->db->group_by("psw_touches.wallpaper_id");
		$this->db->order_by("count(DISTINCT psw_touches.touch_id)", "DESC");
		return $this->db->get();
	
 	}

 	/**
	 * Delete the records by condition
	 *
	 * @param      array   $conds  The conds
	 *
	 * @return     <type>  ( description_of_the_return_value )
	 */
	function get_wallpaper_by_userid( $conds = array(), $limit = false, $offset = false )
	{
		//$this->db->distinct('psw_wallpapers.*');
		$this->db->select('psw_wallpapers.*'); 
		$this->db->from('psw_wallpapers');
		$this->db->join('psw_favourite', 'psw_favourite.wallpaper_id = psw_wallpapers.wallpaper_id');

		if(isset($conds['user_id'])) {

			if ($conds['user_id'] != "" || $conds['user_id'] != 0) {
					
					$this->db->where( 'user_id', $conds['user_id'] );	

			}

		}

		if(isset($conds['is_video_wallpaper'])) {

			if ($conds['is_video_wallpaper'] != "" || $conds['is_video_wallpaper'] != 0) {
					
					$this->db->where( 'is_video_wallpaper', $conds['is_video_wallpaper'] );	

			}

		}

		if(isset($conds['is_wallpaper']) && isset($conds['is_gif']) ) {

			if ($conds['is_wallpaper'] != "" || $conds['is_wallpaper'] != 0 || $conds['is_gif'] != "" || $conds['is_gif'] != 0) {
					
					$this->db->where( 'is_wallpaper', $conds['is_wallpaper'] );	
					$this->db->or_where( 'is_gif', $conds['is_gif'] );

			}

		}

		if ( $limit ) {
		// if there is limit, set the limit
			
			$this->db->limit($limit);
		}
		
		if ( $offset ) {
		// if there is offset, set the offset,
			
			$this->db->offset($offset);
		}
		$this->db->order_by('psw_wallpapers.added_date', "DESC");
		return $this->db->get();
		// print_r($this->db->last_query());die;
 	}

 	/**
	 * Delete the records by condition
	 *
	 * @param      array   $conds  The conds
	 *
	 * @return     <type>  ( description_of_the_return_value )
	 */
	function get_wallpaper_delete_by_userid( $conds = array(), $limit = false, $offset = false )
	{
		$this->db->select('psw_wallpapers.*'); 
		$this->db->from('psw_wallpapers');

		if(isset($conds['added_user_id'])) {

			if ($conds['added_user_id'] != "" || $conds['added_user_id'] != 0) {
					
					$this->db->where( 'added_user_id', $conds['added_user_id'] );	

			}

		}

		if(isset($conds['wallpaper_id'])) {

			if ($conds['wallpaper_id'] != "" || $conds['wallpaper_id'] != 0) {
					
					$this->db->where( 'wallpaper_id', $conds['wallpaper_id'] );	

			}

		}

		if ( $limit ) {
		// if there is limit, set the limit
			
			$this->db->limit($limit);
		}
		
		if ( $offset ) {
		// if there is offset, set the offset,
			
			$this->db->offset($offset);
		}
		return $this->db->get();
 	}

 	/**
	 * Delete the records by condition
	 *
	 * @param      array   $conds  The conds
	 *
	 * @return     <type>  ( description_of_the_return_value )
	 */
	function get_download_by_userid( $conds = array(), $limit = false, $offset = false )
	{
		$this->db->distinct('psw_wallpapers.*');
		$this->db->select('psw_wallpapers.*'); 
		$this->db->from('psw_wallpapers');
		$this->db->join('psw_downloads', 'psw_downloads.wallpaper_id = psw_wallpapers.wallpaper_id');

		if(isset($conds['user_id'])) {

			if ($conds['user_id'] != "" || $conds['user_id'] != 0) {
					
					$this->db->where( 'user_id', $conds['user_id'] );	

			}

		}

		if(isset($conds['is_video_wallpaper'])) {

			if ($conds['is_video_wallpaper'] != "" || $conds['is_video_wallpaper'] != 0) {
					
					$this->db->where( 'is_video_wallpaper', $conds['is_video_wallpaper'] );	

			}

		}

		if(isset($conds['is_wallpaper']) && isset($conds['is_gif']) ) {

			if ($conds['is_wallpaper'] != "" || $conds['is_wallpaper'] != 0 || $conds['is_gif'] != "" || $conds['is_gif'] != 0) {
					
					$this->db->where( 'is_wallpaper', $conds['is_wallpaper'] );	
					$this->db->or_where( 'is_gif', $conds['is_gif'] );

			}

		}


		if ( $limit ) {
		// if there is limit, set the limit
			
			$this->db->limit($limit);
		}
		
		if ( $offset ) {
		// if there is offset, set the offset,
			
			$this->db->offset($offset);
		}

		$this->db->order_by("psw_wallpapers.added_date", "DESC");

		// $this->db->order_by("count(DISTINCT psw_wallpapers.wallpaper_id)", "DESC");
		return $this->db->get();
		// print_r($this->db->last_query());die;
 	}


 	

 	/**
	 * Delete the records by condition
	 *
	 * @param      array   $conds  The conds
	 *
	 * @return     <type>  ( description_of_the_return_value )
	 */
	function get_rating_count( $conds = array() )
	{

		$this->db->select(' sum(rating) as t_count'); 
		$this->db->from('psw_rating');

		if(isset($conds['wallpaper_id'])) {

			if ($conds['wallpaper_id'] != "" || $conds['wallpaper_id'] != 0) {
					
					$this->db->where( 'wallpaper_id', $conds['wallpaper_id'] );	

			}

		}

	
	 	return $this->db->get();
		// print_r($this->db->last_query());die;
 	}

 	/**
	 * Delete the records by condition
	 *
	 * @param      array   $conds  The conds
	 *
	 * @return     <type>  ( description_of_the_return_value )
	 */
	function get_wallpaper_rating_count( $conds = array() )
	{
		
		$this->db->select(' count(wallpaper_id) as w_count'); 
		$this->db->from('psw_rating');

		if(isset($conds['wallpaper_id'])) {

			if ($conds['wallpaper_id'] != "" || $conds['wallpaper_id'] != 0) {
					
					$this->db->where( 'wallpaper_id', $conds['wallpaper_id'] );	

			}

		}

	
	 	return $this->db->get();
		// print_r($this->db->last_query());die;
 	}


 	function get_total_earning_point($conds = array()) {

 		$this->db->select(' sum(earn_point) as total'); 
		$this->db->from('psw_earning_points');

		if(isset($conds['user_id'])) {

			if ($conds['user_id'] != "" || $conds['user_id'] != 0) {
					
					$this->db->where( 'user_id', $conds['user_id'] );	

			}

		}

	 	return $this->db->get();
		// print_r($this->db->last_query());die;
 	}

 	/**
	 * Delete the records by condition
	 *
	 * @param      array   $conds  The conds
	 *
	 * @return     <type>  ( description_of_the_return_value )
	 */
	function get_user_wallpaper( $conds = array() )
	{
		$this->db->select('psw_earning_points_history.*'); 
		$this->db->from('psw_earning_points_history');

		if(isset($conds['user_id'])) {

			if ($conds['user_id'] != "" || $conds['user_id'] != 0) {
					
					$this->db->where( 'user_id', $conds['user_id'] );	

			}

		}
		if(isset($conds['wallpaper_id'])) {

			if ($conds['wallpaper_id'] != "" || $conds['wallpaper_id'] != 0) {
					
					$this->db->where( 'wallpaper_id', $conds['wallpaper_id'] );	

			}

		}
		
		return $this->db->get();
 	}


 	// get user with status 2 for request code

	function user_exists( $conds = array()) {

		$sql = "SELECT * FROM core_users WHERE `user_email` = '" . $conds['user_email'] . "' AND `status` = '" . $conds['status'] . "' ";

		$query = $this->db->query($sql);

		return $query;
	}

	// get user with email conds

	function get_one_user_email( $conds = array()) {

		$sql = "SELECT * FROM core_users WHERE `user_email` = '" . $conds['user_email'] . "' ";

		$query = $this->db->query($sql);

		return $query;
	}

	// get user with status 2 for verify code

	function get_one_user( $conds = array()) {

		$sql = "SELECT * FROM core_users WHERE `status` = '" . $conds['status'] . "' AND `code` = '" . $conds['code'] . "' ";

		$query = $this->db->query($sql);

		return $query;
	}


	// get user with status 2 for verify code

	function get_all_shuffle() {

		$sql = "SELECT * FROM psw_shuffle ";

		$query = $this->db->query($sql);

		return $query;
	}


}