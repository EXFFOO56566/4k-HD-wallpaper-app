<?php
defined('BASEPATH') OR exit('No direct script access allowed');

/**
 * Send Booking Request Email to hotel
 * @param  [type] $booking_id [description]
 * @return [type]             [description]
 */
if ( !function_exists( 'send_user_register_email' )) {

  function send_user_register_email( $user_id, $subject = "" )
  {
    // get ci instance
    $CI =& get_instance();
    
    $user_info_obj = $CI->User->get_one($user_id);

    $user_name  = $user_info_obj->user_name;
    $user_email = $user_info_obj->user_email;
    $code = $user_info_obj->code;
    

    $to = $user_email;

		$sender_name = $CI->config->item( 'sender_name' );

    $msg = <<<EOL
<p>Hi {$user_name},</p>

<p>Your new User Account has been created. Welcome to PS Wallpaper. Please verified with the code at below to actived your account.</p>

<p>
Verified Code : {$code}<br/>
</p>


<p>
Best Regards,<br/>
{$sender_name}
</p>
EOL;
    
    
    

    // send email from admin
    return $CI->ps_mail->send_from_admin( $to, $subject, $msg );
  }
}

if ( !function_exists( 'send_contact_us_emails' )) {

  function send_contact_us_emails( $contact_id, $subject = "" )
  {
    // get ci instance  
    $CI =& get_instance();
    
    $contact_info_obj = $CI->Contact->get_one($contact_id);

    $contact_name  = $contact_info_obj->contact_name;
    $contact_email = $contact_info_obj->contact_email;
    $contact_phone = $contact_info_obj->contact_phone;
    $contact_msg   = $contact_info_obj->contact_message;
    

    $to = $CI->config->item( 'receive_email' );

  $sender_name = $CI->config->item( 'sender_name' );

    $msg = <<<EOL
<p>Hi Admin,</p>

<p>
Name : {$contact_name}<br/>
Email : {$contact_email}<br/>
Phone : {$contact_phone}<br/>
Message : {$contact_msg}<br/>
</p>


<p>
Best Regards,<br/>
{$sender_name}
</p>
EOL;
    
    
    

    // send email from admin
    return $CI->ps_mail->send_from_admin( $to, $subject, $msg );
  }
}
