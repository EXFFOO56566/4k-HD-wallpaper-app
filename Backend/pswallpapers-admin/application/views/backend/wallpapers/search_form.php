<?php
	$attributes = array('id' => 'search-form', 'enctype' => 'multipart/form-data');
	echo form_open( $module_site_url .'/search', $attributes);
?>

<div class='row my-3'>
	<div class='form-inline'>
		<div class="form-group">

			<?php echo form_input(array(
				'name' => 'searchterm',
				'value' => set_value( 'searchterm', $searchterm ),
				'class' => 'form-control form-control-sm mr-3',
				'placeholder' => 'Search'
			)); ?>

	  	</div>

	  	<div class="form-group">

			<?php
				$options=array();
				$options[0]=get_msg('search_cat');
				foreach ($this->Category->get_all()->result() as $cat) {
					
						$options[$cat->cat_id]=$cat->cat_name;
				}

				echo form_dropdown(
					'cat_id',
					$options,
					set_value( 'cat_id', show_data( $cat_id), false ),
					'class="form-control form-control-sm mr-3" id="cat_id"'
				);
			?>

	  	</div>

	  	<div class="form-group">

			<?php
				$options=array();
				$options[0]=get_msg('select_color');
				foreach ($this->Color->get_all()->result() as $color) {

					$options[$color->id]=$color->name;
								
				}
				echo form_dropdown(
					'color_id',
					$options,
					set_value( 'color_id', show_data( $color_id), false ),
					'class="form-control form-control-sm mr-3" id="color_id"'
				);
			?>

	  	</div>

	  	<div class="form-group">

			<?php
				$options=array();
				$options[0]=get_msg('select_type');

				foreach ($this->Type->get_all()->result() as $ty) {

					$options[$ty->id]=$ty->name;
								
				}
				echo form_dropdown(
					'types',
					$options,
					set_value( 'types', show_data( $types), false ),
					'class="form-control form-control-sm mr-3" id="types"'
				);
			?>

	  	</div>

	  	<div class="form-group" >
				<div class="form-check">

					<label class="form-check-label">
					
					<?php echo get_msg( 'from_point' ); ?>
					<?php echo form_input(array(
						'name' => 'point_min',
						'value' => set_value( 'point_min', $point_min ),
						'class' => 'form-control form-control-sm mr-3 ml-2',
						'placeholder' => 'Form',
						'pattern' => "^[0-9]*$",
						'type' => "number"
					)); ?>


					<?php echo get_msg( 'to_point' ); ?>
					<?php echo form_input(array(
						'name' => 'point_max',
						'value' => set_value('point_max', $point_max ),
						'class' => 'form-control form-control-sm mr-3 ml-2',
						'placeholder' => 'To',
						'pattern' => "^[0-9]*$",
						'type' => "number"
					)); ?>

					</label>
				</div>
			</div>

	</div>
</div>
<div class="row my-3">	
	<!-- end form-inline -->
	<div class="form-inline">
		
		<div class="form-group">
			<div class="form-check">

				<label class="form-check-label">
				
				<?php echo form_checkbox( array(
					'name' => 'is_recommended',
					'id' => 'is_recommended',
					'value' => 'is_recommended',
					'checked' => ($is_recommended == 1 )? true: false ,
					'class' => 'form-check-input'
				));	?>

				<?php echo get_msg( 'wallpaper_is_recommended' ); ?>

				</label>
			</div>
		</div>

		<div class="form-group" style="padding-left: 10px;">
			<div class="form-check">

				<label class="form-check-label">
				
				<?php 
				 
				echo form_checkbox( array(
					'name' => 'is_portrait',
					'id' => 'is_portrait',
					'value' => 'is_portrait',
					'checked' =>  ($is_portrait == 1 )? true: false ,
					'class' => 'form-check-input'
				));	?>

				<?php echo get_msg( 'wallpaper_is_portrait' ); ?>

				</label>
			</div>
		</div>

		<div class="form-group" style="padding-left: 10px;">
			<div class="form-check">

				<label class="form-check-label">
				
				<?php echo form_checkbox( array(
					'name' => 'is_landscape',
					'id' => 'is_landscape',
					'value' => 'is_landscape',
					'checked' => ($is_landscape == 1 )? true: false ,
					'class' => 'form-check-input'
				));	?>

				<?php echo get_msg( 'wallpaper_is_landscape' ); ?>

				</label>
			</div>
		</div>

		<div class="form-group" style="padding-left: 10px;">
			<div class="form-check">

				<label class="form-check-label">
				
				<?php echo form_checkbox( array(
					'name' => 'is_square',
					'id' => 'is_square',
					'value' => 'is_square',
					'checked' => ($is_square == 1 )? true: false ,
					'class' => 'form-check-input'
				));	?>

				<?php echo get_msg( 'wallpaper_is_square' ); ?>

				</label>
			</div>
		</div>

		<div class="form-group" style="padding-left: 10px;">
			<div class="form-check">

				<label class="form-check-label">
				
				<?php echo form_checkbox( array(
					'name' => 'is_gif',
					'id' => 'is_gif',
					'value' => 'is_gif',
					'checked' => ($is_gif == 1 )? true: false ,
					'class' => 'form-check-input'
				));	?>

				<?php echo get_msg( 'wallpaper_is_gif' ); ?>

				</label>
			</div>
		</div>

			<div class="form-group" style="padding-left: 10px;">
			<div class="form-check">

				<label class="form-check-label">
				
				<?php echo form_checkbox( array(
					'name' => 'is_video_wallpaper',
					'id' => 'is_video_wallpaper',
					'value' => 'is_video_wallpaper',
					'checked' => ($is_video_wallpaper == 1 )? true: false ,
					'class' => 'form-check-input'
				));	?>

				<?php echo get_msg( 'is_wallpaper_video' ); ?>

				</label>
			</div>
		</div>
</div>
</div>


<div class="row my-3">	
	<!-- end form-inline -->
	<div class="form-inline">
		<div class="form-group">

			<?php

				echo get_msg( 'order_by' );
				//echo $order_by . " #####";

				$options=array();
				$options[0]=get_msg('select_order');

				foreach ($this->Order->get_all()->result() as $ord) {

					$options[$ord->id]=$ord->name;
								
				}
				echo form_dropdown(
					'order_by',
					$options,
					set_value( 'order_by', show_data( $order_by), false ),
					'class="form-control form-control-sm mr-3 ml-3" id="order_by"'
				);
			?>

	  	</div>
	</div>
</div>		


<div class="row my-3">	
	<!-- end form-inline -->
	<div class="form-inline">
		

			<div class="form-group">
			  	<button type="submit" value="submit" name="submit" class="btn btn-sm btn-primary">
			  		<?php echo get_msg( 'btn_search' )?>
			  	</button>
		  	</div>

		  	<div class="row">
		  		<div class="form-group ml-3">
				  	<a href="<?php echo $module_site_url; ?>" class="btn btn-sm btn-primary">
						  		<?php echo get_msg( 'btn_reset' ); ?>
					</a>
				</div>
			</div>
			</div>

		</div>
	</div>

</div>

<div class='row my-3'>
	<div class='col-12'>
		<a href='<?php echo $module_site_url .'/add';?>' class='btn btn-sm btn-primary pull-right'>
			<span class='fa fa-plus'></span> 
			<?php echo get_msg( 'wallpaper_add' )?>
		</a>
	</div>
</div>
<?php echo form_close(); ?>