<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>
  	<?php echo $title; ?>
		<?php 
			if ( isset( $action_title )) {
				echo " | ";
				if ( is_string( $action_title )) echo $action_title;
				else if ( is_array( $action_title )) echo $action_title[count($action_title) - 1]['label'];
			} 
	?>
  </title>
  	<?php
  		$conds = array( 'img_type' => 'fav', 'img_parent_id' => 'abt1' );
		$images = $this->Image->get_all_by( $conds )->result();
	?>
  <link rel="icon" href="<?php echo img_url( $images[0]->img_path ); ?>">
  <!-- Tell the browser to be responsive to screen width -->
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="<?php echo base_url('assets/backend/css/animate.css'); ?>">
  <link rel="stylesheet" href="<?php echo base_url('assets/backend/css/style.css'); ?>">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="<?php echo base_url('assets/plugins/font-awesome/css/font-awesome.min.css'); ?>">
	<!-- Ionicons -->
	<link rel="stylesheet" href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
	<!-- Theme style -->
	<link rel="stylesheet" href="<?php echo base_url('assets/dist/css/AdminLTE.css'); ?>">
	<!-- iCheck -->
	<link rel="stylesheet" href="<?php echo base_url('assets/plugins/iCheck/flat/blue.css'); ?>">
	<!-- Morris chart -->
	<link rel="stylesheet" href="<?php echo base_url('assets/plugins/morris/morris.css'); ?>">
	<!-- jvectormap -->
	<link rel="stylesheet" href="<?php echo base_url('assets/plugins/jvectormap/jquery-jvectormap-1.2.2.css'); ?>">

	<!-- Color Picker -->
	<link href="<?php echo base_url('assets/plugins/colorpicker/bootstrap-colorpicker.min.css');?>" rel="stylesheet">

	<!-- Date Picker -->
	<link rel="stylesheet" href="<?php echo base_url('assets/plugins/datepicker/datepicker3.css'); ?>">
	<!-- Daterange picker -->
	<link rel="stylesheet" href="<?php echo base_url('assets/plugins/daterangepicker/daterangepicker-bs3.css'); ?>">
	<!-- bootstrap wysihtml5 - text editor -->
	<link rel="stylesheet" href="<?php echo base_url('assets/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css'); ?>">
  	<!-- Google Font: Source Sans Pro -->
  	<link href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700" rel="stylesheet">

	<!-- ChartJS 1.0.1 -->
	<script src="<?php echo base_url( 'assets/plugins/chartjs-old/Chart.min.js' ); ?>"></script>
	<!-- jQuery -->
    <script src="<?php echo base_url( 'assets/plugins/jquery/jquery.min.js' ); ?>"></script>
    <!-- Image Popup link -->
   	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/magnific-popup.js/1.1.0/magnific-popup.min.css">
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/magnific-popup.js/1.1.0/jquery.magnific-popup.min.js"></script>
	<link href="https://fonts.googleapis.com/css?family=Roboto+Mono|Work+Sans" rel="stylesheet">

</head>
<body id="<?php echo strtolower( $module_name ); ?>">
<div class="wrapper">