<?php
require_once( APPPATH .'libraries/REST_Controller.php' );

/**
 * REST API for Users
 */
class Users extends API_Controller
{

	/**
	 * Constructs Parent Constructor
	 */
	function __construct()
	{
		parent::__construct( 'User' );
	}	

	/**
	 * Convert Object
	 */
	function convert_object( &$obj )
	{
		// call parent convert object
		parent::convert_object( $obj );

		// convert customize category object
		$this->ps_adapter->convert_user( $obj );
	}
	
	/**
	 * Users Registration
	 */
	function add_post()
	{
		// validation rules for user register
		$rules = array(
			array(
	        	'field' => 'user_name',
	        	'rules' => 'required'
	        ),
	        array(
	        	'field' => 'user_email',
	        	'rules' => 'required|valid_email|callback_email_check'
	        ),
	        array(
	        	'field' => 'user_password',
	        	'rules' => 'required'
	        )

        );

		// exit if there is an error in validation,
        if ( !$this->is_valid( $rules )) exit;

        $code = generate_random_string(5);

        $user_data = array(
        	
        	"user_name" => $this->post('user_name'), 
        	"user_email" => $this->post('user_email'), 
        	'user_password' => md5($this->post('user_password')),
        	"device_token" => $this->post('device_token'),
        	"code" =>  $code,
        	"email_verify" => 1,
        	"status" => 2 //Need to verified status

        );

        $conds['user_email'] = $user_data['user_email'];
        $conds['status'] = 2;
       	$user_infos = $this->User->user_exists($conds)->result();

       	if (empty($user_infos)) {

       		if ( !$this->User->save($user_data)) {

        	$this->error_response( get_msg( 'err_user_register' ));

        	} else {

	        	$subject = "New User Account Registration";
				

	        	if ( !send_user_register_email( $user_data['user_id'], $subject )) {

					$this->error_response( get_msg( 'user_register_success_but_email_not_send' ));
				
				} 
        	}

       	} else {

       		//$this->error_response( get_msg( 'need_to_verify' ));

       		$user_id = $user_infos[0]->user_id;
       		$subject = "New User Account Registration";

       		if ( !send_user_register_email( $user_id, $subject )) {

					$this->error_response( get_msg( 'user_register_success_but_email_not_send' ));
				
				} 

       		$this->custom_response($this->User->get_one($user_id));

       	}

        
        $this->custom_response($this->User->get_one($user_data["user_id"]));

	}

	/**
	 * Users Registration with Facebook
	*/
	function register_post()
	{
		$rules = array(
			array(
	        	'field' => 'user_name',
	        	'rules' => 'required'
	        ),
	        array(
	        	'field' => 'facebook_id',
	        	'rules' => 'required'
	        )
        );

		// exit if there is an error in validation,
        if ( !$this->is_valid( $rules )) exit;

        //Need to check facebook_id is aleady exist or not?
        if ( !$this->User->exists( array( 'facebook_id' => $this->post( 'facebook_id' ) ))) {

            //User not yet exist 
        	$fb_id = $this->post( 'facebook_id' ) ;
			$url = "https://graph.facebook.com/$fb_id/picture?width=350&height=500";
		  	$data = file_get_contents($url);
		  	
		  	
		  	$dir = "uploads/";
			$img = md5(time()).'.jpg';
		  	$ch = curl_init($url);
			$fp = fopen( 'uploads/'. $img, 'wb' );
			curl_setopt($ch, CURLOPT_FILE, $fp);
			curl_setopt($ch, CURLOPT_HEADER, 0);
			curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
			curl_exec($ch);
			curl_close($ch);
			fclose($fp);


			////

			$user_data = array(
	        	"user_name" 	=> $this->post('user_name'), 
	        	'user_email'    => $this->post('user_email'), 
	        	"facebook_id" 	=> $this->post('facebook_id'),
	        	"user_profile_photo" => $img,
	        	"device_token" => $this->post('device_token'),
	        	"facebook_verify" => 1,
	        	"status" 	=> 1, 
		        "code"    => ' ',
		        "user_password" => ' '
        	);


        	$user_email = $user_data['user_email'];
        	//print_r($user_email);die;

        	if (!empty($user_email)) {
        		//email exists
        		$conds_email['user_email'] = $user_email;
        		$user_infos = $this->User->get_one_user_email($conds_email)->result();
				$user_id = $user_infos[0]->user_id;
        		
        	} 
			
        	if ( $user_id != "") {
				//user email alerady exist

				$this->User->save($user_data,$user_id);
				
			} else {
				//user email not exist

				if ( !$this->User->save($user_data)) {
        			$this->error_response( get_msg( 'err_user_register' ));
        		}

        		$this->custom_response($this->User->get_one($user_data['user_id']));

			}

        	$this->custom_response($this->User->get_one($user_infos[0]->user_id));

        } else {

        	//User already exist in DB
        	$conds['facebook_id'] = $this->post( 'facebook_id' );
        	$user_profile_photo = $this->User->get_one_by($conds['facebook_id'])->user_profile_photo;

        	//Delete existing image 
        	@unlink('./uploads/'.$user_profile_photo);
			
			//Download again
			$fb_id = $this->post( 'facebook_id' ) ;
			$url = "https://graph.facebook.com/$fb_id/picture?width=350&height=500";
		  	$data = file_get_contents($url);
		  	
		  	
		  	$dir = "uploads/";
			$img = md5(time()).'.jpg';
		  	$ch = curl_init($url);
			$fp = fopen( 'uploads/'. $img, 'wb' );
			curl_setopt($ch, CURLOPT_FILE, $fp);
			curl_setopt($ch, CURLOPT_HEADER, 0);
			curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
			curl_exec($ch);
			curl_close($ch);
			fclose($fp);

			$user_data = array(
				'user_name'    	=> $this->post('user_name'), 
				'user_email'    => $this->post('user_email'),
				'user_profile_photo' => $img,
				'device_token'  => $this->post('device_token')
			);

			$conds['facebook_id'] = $this->post( 'facebook_id' );
			$user_datas = $this->User->get_one_by($conds);
			$user_id = $user_datas->user_id;

			if ( $user_datas->is_banned == 1 ) {

				$this->error_response( get_msg( 'err_user_banned' ));
			} else {

				if ( !$this->User->save($user_data,$user_id)) {
        		$this->error_response( get_msg( 'err_user_register' ));
        		}

			}

        	$this->custom_response($this->User->get_one($user_datas->user_id));

        }

	}
	

	/**
	 * Users Registration with Google
	*/
	function google_register_post()
	{
		$rules = array(
			array(
	        	'field' => 'user_name',
	        	'rules' => 'required'
	        ),
	        array(
	        	'field' => 'google_id',
	        	'rules' => 'required'
	        )
        );

		// exit if there is an error in validation,
        if ( !$this->is_valid( $rules )) exit;

        //Need to check google_id is aleady exist or not?
        if ( !$this->User->exists( 
        	array( 
        		'google_id' => $this->post( 'google_id' ) 
        		))) {
        
            //User not yet exist 
        	$gg_id = $this->post( 'google_id' ) ;
			$url = $this->post('profile_photo_url');

			if ($url !="") {

				$data = file_get_contents($url);
			  	$dir = "uploads/";
				$img = md5(time()).'.jpg';
			  	$ch = curl_init($url);
				$fp = fopen( 'uploads/'. $img, 'wb' );
				curl_setopt($ch, CURLOPT_FILE, $fp);
				curl_setopt($ch, CURLOPT_HEADER, 0);
				curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
				curl_exec($ch);
				curl_close($ch);
				fclose($fp);

				$user_data = array(
		        	"user_name" 	=> $this->post('user_name'), 
		        	'user_email'    => $this->post('user_email'), 
		        	"google_id" 	=> $this->post('google_id'),
		        	"user_profile_photo" => $img,
		        	"device_token" => $this->post('device_token'),
		        	"google_verify" => 1,
		        	"status" 	=> 1, 
			        "code"   => ' ',
			        "user_password" => ' '
	        	);

			} else{

					$user_data = array(
		        	"user_name" 	=> $this->post('user_name'), 
		        	'user_email'    => $this->post('user_email'), 
		        	"google_id" 	=> $this->post('google_id'),
		        	"device_token" => $this->post('device_token'),
		        	"google_verify" => 1,
		        	"status" 	=> 1, 
			        "code"   => ' ',
			        "user_password" => ' '
        		);
			}

        	$conds_email['user_email'] = $user_data['user_email'];
			$user_infos = $this->User->get_one_user_email($conds_email)->result();
			$user_id = $user_infos[0]->user_id;

			if ( $user_id != "") {
				//user email alerady exist

				$this->User->save($user_data,$user_id);
				
			} else {
				//user email not exist

				if ( !$this->User->save($user_data)) {
        		$this->error_response( get_msg( 'err_user_register' ));
        		}

        		$this->custom_response($this->User->get_one($user_data['user_id']));

			}

        	$this->custom_response($this->User->get_one($user_infos[0]->user_id));

        } else {

        	//User already exist in DB
        	$conds['google_id'] = $this->post( 'google_id' );
        	$user_profile_photo = $this->User->get_one_by($conds['google_id'])->user_profile_photo;

        	//Delete existing image 
        	@unlink('./uploads/'.$user_profile_photo);
			
			//Download again
			$gg_id = $this->post( 'google_id' ) ;
			$url = $this->post('profile_photo_url');
		  	
			if($url != "") {
			  	$data = file_get_contents($url);
			  	
			  	
			  	$dir = "uploads/";
				$img = md5(time()).'.jpg';
			  	$ch = curl_init($url);
				$fp = fopen( 'uploads/'. $img, 'wb' );
				curl_setopt($ch, CURLOPT_FILE, $fp);
				curl_setopt($ch, CURLOPT_HEADER, 0);
				curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
				curl_exec($ch);
				curl_close($ch);
				fclose($fp);

				$user_data = array(
					'user_name'    	=> $this->post('user_name'), 
					'user_email'    => $this->post('user_email'), 
					'user_profile_photo' => $img,	
				);
			} else {

				$user_data = array(
					'user_name'    	=> $this->post('user_name'), 
					'user_email'    => $this->post('user_email')
				);
			}
			$conds['google_id'] = $this->post( 'google_id' );
			$user_datas = $this->User->get_one_by($conds);
			$user_id = $user_datas->user_id;

			if ( $user_datas->is_banned == 1 ) {

				$this->error_response( get_msg( 'err_user_banned' ));
			} else {

				if ( !$this->User->save($user_data,$user_id)) {
	        		$this->error_response( get_msg( 'err_user_register' ));
	        	}

			}

        	$this->custom_response($this->User->get_one($user_datas->user_id));

        }


	}

	/**
	 * Email Checking
	 *
	 * @param      <type>  $email     The identifier
	 *
	 * @return     <type>  ( description_of_the_return_value )
	 */
	function email_check( $email )
    {

        if ( $this->User->exists( array( 'user_email' => $email ))) {
        
            $this->form_validation->set_message('email_check', 'Email Exist');
            return false;
        }

        return true;
    }

    /**
	 * Users Login
	 */
	function login_post()
	{
		// validation rules for user register
		$rules = array(
			
	        array(
	        	'field' => 'user_email',
	        	'rules' => 'required|valid_email'
	        ),
	        array(
	        	'field' => 'user_password',
	        	'rules' => 'required'
	        )
        );

		// exit if there is an error in validation,
        if ( !$this->is_valid( $rules )) exit;
        
        if ( $this->User->exists( array( 'user_email' => $this->post( 'user_email' ), 'user_password' => $this->post( 'user_password' ), 'device_token' => $this->post( 'device_token' )))) {

        //if ( $this->User->exists( array( 'user_email' => $this->post( 'user_email' ), 'user_password' => $this->post( 'user_password' )))) {

        	$email = $this->post( 'user_email' );
	        $conds['user_email'] = $email;
	        $is_banned = $this->User->get_one_by($conds)->is_banned;

	        if ( $is_banned == '1') {
	        	$this->error_response( get_msg( 'err_user_banned' ));
	        } else {
	        	$user = $this->User->get_one_by(array("user_email" => $this->post('user_email')));
        		
		        $this->custom_response($user);

	        }
        
            
        } else {

        	$this->error_response( get_msg( 'err_user_not_exist' ));

        }

	}

	/**
	* User Reset Password
	*/
	function reset_post()
	{
		// validation rules for user register
		$rules = array(
	        array(
	        	'field' => 'user_email',
	        	'rules' => 'required|valid_email'
	        )
        );

		// exit if there is an error in validation,
        if ( !$this->is_valid( $rules )) exit;

        $user_info = $this->User->get_one_by( array( "user_email" => $this->post( 'user_email' )));

        if ( isset( $user_info->is_empty_object )) {
        // if user info is empty,
        	
        	$this->error_response( get_msg( 'err_user_not_exist' ));
        }

        // generate code
        $code = md5(time().'teamps');

        // insert to reset
        $data = array(
			'user_id' => $user_info->user_id,
			'code' => $code
		);

		if ( !$this->ResetCode->save( $data )) {
		// if error in inserting,

			$this->error_response( get_msg( 'err_model' ));
		}

		// Send email with reset code
		$to = $user_info->user_email;
	    $subject = 'Password Reset';
		$msg = "<p>Hi,". $user_info->user_name ."</p>".
					"<p>Please click the following link to reset your password<br/>".
					"<a href='". site_url( $this->config->item( 'reset_url') .'/'. $code ) ."'>Reset Password</a></p>".
					"<p>Best Regards,<br/>". $this->config->item('sender_name') ."</p>";

		// send email from admin
		if ( ! $this->ps_mail->send_from_admin( $to, $subject, $msg ) ) {

			$this->error_response( get_msg( 'err_email_not_send' ));
		}
		
		$this->success_response( get_msg( 'success_email_sent' ));
	}

	/**
	* User Profile Update
	*/

	function profile_update_post()
	{

		// validation rules for user register
		$rules = array(
			array(
	        	'field' => 'user_id',
	        	'rules' => 'required'
	        ),
	        array(
	        	'field' => 'user_name',
	        	'rules' => 'required'
	        ),
	        array(
	        	'field' => 'user_email',
	        	'rules' => 'required|valid_email'
	        ),
	        array(
	        	'field' => 'user_phone',
	        	'rules' => 'required'
	        ),
	        array(
	        	'field' => 'user_about_me',
	        	'rules' => 'required'
	        )
        );

		// exit if there is an error in validation,
        if ( !$this->is_valid( $rules )) exit;

        
        $user_data = array(
        	"user_name"     => $this->post('user_name'), 
        	"user_email"    => $this->post('user_email'), 
        	"user_phone"    => $this->post('user_phone'),
        	"user_about_me" => $this->post('user_about_me'),
        	"device_token" => $this->post('device_token')
        );
        // print_r($user_data);die;

        if ( !$this->User->save($user_data, $this->post('user_id'))) {

        	$this->error_response( get_msg( 'err_user_update' ));
        }

        $this->success_response( get_msg( 'success_profile_update' ));

	}

	/**
	* User Profile Update
	*/
	function password_update_put()
	{

		// validation rules for user register
		$rules = array(
			array(
	        	'field' => 'user_id',
	        	'rules' => 'required|callback_id_check[User]'
	        ),
	        array(
	        	'field' => 'user_password',
	        	'rules' => 'required'
	        )
        );

		// exit if there is an error in validation,
        if ( !$this->is_valid( $rules )) exit;

        $user_data = array(
        	"user_password"     => md5($this->put('user_password'))
        );

        if ( !$this->User->save($user_data, $this->put('user_id'))) {
        	$this->error_response( get_msg( 'err_user_password_update' )); 
        }

        $this->success_response( get_msg( 'success_profile_update' ));

	}

	/**
	* User Verified Code
	*/
	function verify_post()
	{

		// validation rules for user register
		$rules = array(
			array(
	        	'field' => 'user_id',
	        	'rules' => 'required|callback_id_check[User]'
	        ),
	        array(
	        	'field' => 'code',
	        	'rules' => 'required'
	        )
        );

		// exit if there is an error in validation,
        if ( !$this->is_valid( $rules )) exit;

        $user_verify_data = array(
        	"code"     => $this->post('code'),
        	"user_id"  => $this->post('user_id'),
        	"status"   => 2		
        );

        $user_data = $this->User->get_one_user($user_verify_data)->result();

        foreach ($user_data as $user) {
        	$user_id = $user->user_id;
        	$code = $user->code;
        }

        if($user_id  == $this->post('user_id')) {
        	$user_data = array(
	        	"code"    => " ",
	        	"status"  => 1
        	);
        	$this->User->save($user_data,$user_id);
        	$this->custom_response($this->User->get_one($user_id));

        } else {

        	$this->error_response( get_msg( 'invalid_code' )); 

        }

        

	}
	/**
	 * Users Request Code
	 */
	function request_code_post()
	{
		// validation rules for user register
		$rules = array(
	        array(
	        	'field' => 'user_email',
	        	'rules' => 'required'
	        )

        );

		// exit if there is an error in validation,
        if ( !$this->is_valid( $rules )) exit;

        	if (!$this->User->user_exists( array( 'user_email' => $this->post( 'user_email' ), 'status' => 2 ))) {

        		$this->error_response( get_msg( 'err_user_not_exist' ));

        	}  else {
        		
        		$email = $this->post( 'user_email' );
		        $conds['user_email'] = $email;
		        $conds['status'] = 2;

		        $user_data = $this->User->user_exists($conds)->result();

		       	foreach ($user_data as $user) {
		       		$user_id = $user->user_id;
		       		$code = $user->code;
		       	}

		        if($code == " " ) {

		        	$resend_code = generate_random_string(5);
		        	$user_data_code = array(
			        	"code"    => $resend_code
		        	);
		        	$this->User->save($user_data_code,$user_id);

		        } 

	        
		        $user_data['user_id'] = $user_id;
		        //print_r($user_data);die;

        		$subject = "Verification Code has been sent";

	        	if ( !send_user_register_email( $user_data['user_id'], $subject )) {

					$this->error_response( get_msg( 'user_register_success_but_email_not_send' ));
				
				}
					
				$this->success_response( get_msg( 'success_email_sent' ));

				
        	}

       
    }

    /**
	 * Users Registration with Phone
	*/
	function phone_register_post()
	{
		$rules = array(
			array(
	        	'field' => 'user_name',
	        	'rules' => 'required'
	        ),
	        array(
	        	'field' => 'phone_id',
	        	'rules' => 'required'
	        )
        );

		// exit if there is an error in validation,
        if ( !$this->is_valid( $rules )) exit;

        //Need to check phone_id is aleady exist or not?
        if ( !$this->User->exists( 
        	//new
        	array( 
        		'phone_id' => $this->post( 'phone_id' ) 
        		))) {
        
           

			$user_data = array(
	        	"user_name" 	=> $this->post('user_name'), 
	        	'user_phone'    => $this->post('user_phone'), 
	        	"phone_id" 	   => $this->post('phone_id'),
	        	"device_token" => $this->post('device_token'),
	        	"phone_verify" => 1
        	);

        	if ( !$this->User->save($user_data)) {
        		$this->error_response( get_msg( 'err_user_register' ));
        	}


        	$noti_data = array(

					"user_id" => $user_data['user_id'],
					"device_token" => $user_data['device_token']
				);
		        
        		if ( !$this->Noti->exists( $noti_data )) {
		        	$this->Noti->save( $noti_data, $push_noti_token_id );
		        } 

        	$this->custom_response($this->User->get_one($user_data["user_id"]));

        } else {
        	//update
        	//User already exist in DB
			$user_data = array(
				'user_name'    	=> $this->post('user_name'), 
				'user_phone'    => $this->post('user_phone'),
				"device_token" => $this->post('device_token'),
			);

			$conds['phone_id'] = $this->post( 'phone_id' );
			$user_datas = $this->User->get_one_by($conds);
			$user_id = $user_datas->user_id;

			if ( $user_datas->is_banned == 1 ) {

				$this->error_response( get_msg( 'err_user_banned' ));
			} else {

				if ( !$this->User->save($user_data,$user_id)) {
	        		$this->error_response( get_msg( 'err_user_register' ));
	        	}

	        	$noti_data = array(

					"user_id" => $user_id,
					"device_token" => $user_data['device_token']
				);
		        
        		if ( !$this->Noti->exists( $noti_data )) {
		        	$this->Noti->save( $noti_data, $push_noti_token_id );
		        } 

			}

        	$this->custom_response($this->User->get_one($user_datas->user_id));

        }

	}
}