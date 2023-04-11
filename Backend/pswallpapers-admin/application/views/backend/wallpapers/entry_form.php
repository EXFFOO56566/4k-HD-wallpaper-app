<?php
	$attributes = array( 'id' => 'wallpaper-form', 'enctype' => 'multipart/form-data');
	echo form_open( '', $attributes);
?>
	
<section class="content animated fadeInRight">
	<div class="card card-info">
	    <div class="card-header">
	        <h3 class="card-title"><?php echo get_msg('wallpaper_info')?></h3>
	    </div>
        <!-- /.card-header -->
        <div class="card-body">
            <div class="row">
             	<div class="col-md-6">
            		<div class="form-group">
						<label>
							<span style="font-size: 17px; color: red;">*</span>
							<?php echo get_msg('wallpaper_name')?>
							<a href="#" class="tooltip-ps" data-toggle="tooltip" title="<?php echo get_msg('wallpaper_name_tooltips')?>">
								<span class='glyphicon glyphicon-info-sign menu-icon'>
							</a>
						</label>

						<?php echo form_input( array(
							'name' => 'wallpaper_name',
							'value' => set_value( 'wallpaper_name', show_data( @$wallpaper->wallpaper_name ), false ),
							'class' => 'form-control form-control-sm',
							'placeholder' => get_msg( 'wallpaper_name' ),
							'id' => 'wallpaper_name'
						)); ?>

					</div>

					
					<div class="form-group">
						<label>
							<span style="font-size: 17px; color: red;">*</span>
							<?php echo get_msg('wallpaper_types')?>
						</label>

						<select class="form-control" name="types" id="types">
							<option value="0"><?php echo get_msg('select_wallpaper_types');?></option>

							<?php
							$array = array('Free' => 1, 'Premium' => 2);
		    					foreach ($array as $key=>$value) {
		    						
		    						if($value == $wallpaper->types) {
			    						echo '<option value="'.$value.'" selected>'.$key.'</option>';
			    					} else {
			    						echo '<option value="'.$value.'">'.$key.'</option>';
			    					}
		    					}
							?>
						</select>

					</div>

					<div class="form-group">
						<label>
							<span style="font-size: 17px; color: red;">*</span>
							<?php echo get_msg('wallpaper_point')?>
							<a href="#" class="tooltip-ps" data-toggle="tooltip" title="<?php echo get_msg('wallpaper_point')?>">
								<span class='glyphicon glyphicon-info-sign menu-icon'/>
							</a>
						</label>
						<?php if($wallpaper->types == 1) {?>
							<?php echo form_input( array(
								'name' => 'point',
								'value' => set_value( 'point', show_data( @$wallpaper->point ), false ),
								'class' => 'form-control form-control-sm',
								'placeholder' => get_msg( 'wallpaper_point' ),
								'id' => 'point',
								'disabled' => 'disabled'
							)); ?>

						<?php }else{ ?>
							<?php echo form_input( array(
								'name' => 'point',
								'value' => set_value( 'point', show_data( @$wallpaper->point ), false ),
								'class' => 'form-control form-control-sm',
								'placeholder' => get_msg( 'wallpaper_point' ),
								'id' => 'point'
							)); ?>
						<?php }?>
					</div>

					<label>
						<span style="font-size: 17px; color: red;">*</span>
						<?php echo get_msg('wallpaper_file_upload'); ?></label>
						<div class="form-group" style="padding-top: 30px;">
							<div>
						        <label><input type="radio" name="wallpaperRadio" value="is_wallpaper" <?php 
									       		$is_wallpaper = $wallpaper->is_wallpaper;
									       		$is_gif = $wallpaper->is_gif;
									        if ($is_wallpaper == 1 || $is_gif ==1) echo "checked"; ?> >
						          <?php echo get_msg('is_wallpaper'); ?> </label>
						        <label class="pull-right"><input type="radio" name="wallpaperRadio" value="is_video_wallpaper"  <?php 
									       		$is_video_wallpaper = $wallpaper->is_video_wallpaper;
									        if ($is_video_wallpaper == 1) echo "checked"; ?> > <?php echo get_msg('is_video_wallpaper'); ?> </label>
						        
						    </div>
						    
						    <?php 
						    	if($wallpaper->is_wallpaper == 1 || $wallpaper->is_gif == 1) {
						    		$display = "block";
						    	} else {
						    		$display = "none";
						    	}
						    ?>

						    <div class="is_wallpaper box" style="display: <?php echo $display; ?> "> 

								<?php if ( !isset( $wallpaper )): ?>

									<div class="form-group">
									
										<label>
											<span style="font-size: 17px; color: red;">*</span>
											<?php echo get_msg('wallpaper_img')?>
											<a href="#" class="tooltip-ps" data-toggle="tooltip" title="<?php echo get_msg('wallpaper_photo_tooltips')?>">
												<span class='glyphicon glyphicon-info-sign menu-icon'>
											</a>
										</label>

										<br/>

										<input class="btn btn-sm" type="file" id="images1" name="images1">
										<input type="hidden" name="is_gif" id="is_gif">
									</div>

								<?php else: ?>

									<label>
										<span style="font-size: 17px; color: red;">*</span>
										<?php echo get_msg('wallpaper_img')?>
										<a href="#" class="tooltip-ps" data-toggle="tooltip" title="<?php echo get_msg('wallpaper_photo_tooltips')?>">
											<span class='glyphicon glyphicon-info-sign menu-icon'>
										</a>
									</label> 
						
									<div class="btn btn-sm btn-primary btn-upload pull-right" data-toggle="modal" data-target="#uploadImage">
										<?php echo get_msg('btn_replace_photo')?>
									</div>
									
									<hr/>
					
								<?php
									$conds = array( 'img_type' => 'wallpaper', 'img_parent_id' => $wallpaper->wallpaper_id );
									$images = $this->Image->get_all_by( $conds )->result();
								?>
						
								<?php if ( count($images) > 0 ): ?>
									
									<div class="row">

									<?php $i = 0; foreach ( $images as $img ) :?>

										<?php if ($i>0 && $i%3==0): ?>
												
										</div><div class='row'>
										
										<?php endif; ?>
											
										<div class="col-md-4" style="height:100">

											<div class="thumbnail">
												<?php 
													$photo_width = $default_photo->img_width;
													$photo_height = $default_photo->img_height;
													$width = "";
													$height = "";
													if ( $photo_width > $photo_height ) {
														$width = "450px";
														$height = "300px";
													} elseif ( $photo_width < $photo_height) {
														$width = "240px";
														$height = "300px";
													} else {
														$width = "300px";
														$height = "300px";
													}
												?>
												<?php $image = $img->img_path;
												//print_r($image);die; 
												$tmp_image = explode('.', $image);

												$new_image = $tmp_image[1];

												if ($new_image == "gif") {
												 ?>
									
													<img style="width: <?php echo $width ?>;height: <?php echo $height ?>;" src="<?php echo img_url( '/'. $img->img_path ); ?>"/>

												<?php } else{ ?>
												<a  href="<?php echo $this->ps_image->upload_url . $img->img_path; ?>">
													<img style="width: <?php echo $width ?>;height: <?php echo $height ?>;" src="<?php echo $this->ps_image->upload_thumbnail_url . $img->img_path; ?>">
												</a>
											<?php } ?>
												<br/>
												
												<p class="text-center">
													
													<a data-toggle="modal" data-target="#deletePhoto" class="delete-img" id="<?php echo $img->img_id; ?>"   
														image="<?php echo $img->img_path; ?>">
														Remove
													</a>
												</p>

											</div>

										</div>

									<?php $i++; endforeach; ?>

									</div>
								
									<?php endif; ?>

								<?php endif; ?>	
							</div>
						    
						    <?php 
						    	if($wallpaper->is_video_wallpaper == 1) {
						    		$display = "block";
						    	} else {
						    		$display = "none";
						    	}
						    ?>

						    <div class="is_video_wallpaper box" style="display: <?php echo $display; ?> ">
						    	
						    	<?php if ( !isset( $wallpaper )): ?>
									<div class="form-group">
										<label>
											<span style="font-size: 17px; color: red;">*</span>
											<?php echo get_msg('vid_upload')?>
											<a href="#" class="tooltip-ps" data-toggle="tooltip" title="<?php echo get_msg('video_icon_tooltips')?>">
												<span class='glyphicon glyphicon-info-sign menu-icon'>
											</a>
										</label>

										<br/>

										<input type="file" class="btn btn-sm" accept=".flv,.f4v,.f4p,.mp4" name="video" id="video">

									</div>
								<?php else: ?>

									<label>
										<span style="font-size: 17px; color: red;">*</span>
										<?php echo get_msg('vid_upload')?>
										<a href="#" class="tooltip-ps" data-toggle="tooltip" title="<?php echo get_msg('vid_upload_icon_tooltips')?>">
											<span class='glyphicon glyphicon-info-sign menu-icon'>
										</a>
									</label> 

									<div class="btn btn-sm btn-primary btn-upload pull-right" data-toggle="modal" data-target="#uploadvideo">
										<?php echo get_msg('btn_replace_video')?>
									</div>

									<hr/>
					
								<?php
									$conds = array( 'img_type' => 'video', 'img_parent_id' => $wallpaper->wallpaper_id );
									$videos = $this->Image->get_all_by($conds)->result();
								?>
						
								
									<?php if ( count($videos) > 0 ): ?>
							
										<div class="row">

											<?php $i = 0; foreach ( $videos as $video ) :?>

												<?php if ($i>0 && $i%3==0): ?>
														
												</div><div class='row'>
												
												<?php endif; ?>
												
												<div class="col-md-4">

													<video width="320" height="240" controls>
													    <source src="<?php echo $this->ps_image->upload_url . $video->img_path; ?>" type="video/mp4" / >
													    This text displays if the video tag isn't supported.
													</video>

													<br/>
														
														<p class="text-center">
															
															<a data-toggle="modal" data-target="#deleteVideo" class="delete-video" id="<?php echo $video->img_id; ?>"   
																image="<?php echo $video->img_path; ?>">
																Remove
															</a>
														</p>

												</div>

											<?php $i++; endforeach; ?>

										</div>
						
									<?php endif; ?>
								<?php endif; ?>

								<?php if ( !isset( $wallpaper )): ?>

									<div class="form-group">
									
										<label>
											<span style="font-size: 17px; color: red;">*</span>
											<?php echo get_msg('vid_icon')?>
											<a href="#" class="tooltip-ps" data-toggle="tooltip" title="<?php echo get_msg('video_icon_tooltips')?>">
												<span class='glyphicon glyphicon-info-sign menu-icon'>
											</a>
										</label>

										<br/>

										<input class="btn btn-sm" type="file" id="icon" name="icon">
										<!-- <input type="hidden" name="is_gif" id="is_gif"> -->
									</div>

								<?php else: ?>

									<label>
										<span style="font-size: 17px; color: red;">*</span>
										<?php echo get_msg('vid_icon')?>
										<a href="#" class="tooltip-ps" data-toggle="tooltip" title="<?php echo get_msg('vid_upload_icon_tooltips')?>">
											<span class='glyphicon glyphicon-info-sign menu-icon'>
										</a>
									</label> 
					
									<div class="btn btn-sm btn-primary btn-upload pull-right" data-toggle="modal" data-target="#uploadvideoicon">
										<?php echo get_msg('btn_replace_photo')?>
									</div>
					
									<hr/>
					
								<?php
									$conds = array( 'img_type' => 'video-icon', 'img_parent_id' => $wallpaper->wallpaper_id );
									$videos = $this->Image->get_all_by($conds)->result();
								?>
						
								
									<?php if ( count($videos) > 0 ): ?>
							
										<div class="row">

											<?php $i = 0; foreach ( $videos as $video ) :?>

												<?php if ($i>0 && $i%3==0): ?>
														
												</div><div class='row'>
												
												<?php endif; ?>
												
												<div class="thumbnail">
						
													<a  href="<?php echo $this->ps_image->upload_url . $video->img_path; ?>">
														<img src="<?php echo $this->ps_image->upload_thumbnail_url . $video->img_path; ?>">
													</a>
												
													<br/>
													
													<p class="text-center">
														
														<a data-toggle="modal" data-target="#deletePhoto" class="delete-img" id="<?php echo $video->img_id; ?>"   
															image="<?php echo $video->img_path; ?>">
															Remove
														</a>
													</p>

												</div>

											<?php $i++; endforeach; ?>

										</div>
						
									<?php endif; ?>
					
								<?php endif; ?>

							</div>
						
						</div>
						
                </div>

                <div class="col-md-6">
		            <div class="form-group">
						<label>
							<span style="font-size: 17px; color: red;">*</span>
							<?php echo get_msg( 'cat_name' )?>
							<a href="#" class="tooltip-ps" data-toggle="tooltip" title="<?php echo get_msg('cat_name_tooltips')?>">
								<span class='glyphicon glyphicon-info-sign menu-icon'>
							</a>
						</label>
						
						<?php 
							$options = array();
							$options[0]=get_msg('cat_select');
							foreach ( $this->Category->get_all()->result() as $cat) {

								$options[$cat->cat_id]=$cat->cat_name;
							
							}

							echo form_dropdown(
								'cat_id',
								$options,
								set_value( 'cat_id', show_data( @$wallpaper->cat_id), false ),
								'class="form-control form-control-sm mr-3" id="cat_id"'
							);
						?>
						
					</div>

					<div class="form-group">
						<label>
							<?php echo get_msg('select_color')?>
						</label>

						<?php
							$options=array();
							$options[0]=get_msg('select_color');
							foreach ($this->Color->get_all()->result() as $color) {

								$options[$color->id]=$color->name;
								
							}

							echo form_dropdown(
								'color_id',
								$options,
								set_value( 'color_id', show_data( @$wallpaper->color_id), false ),
								'class="form-control form-control-sm mr-3" id="color_id"'
							);
						?>

					</div>

                  	<div class="form-group">
						<label>
							<?php echo get_msg('wallpaper_search_tags')?>
							<a href="#" class="tooltip-ps" data-toggle="tooltip" title="<?php echo get_msg('wallpaper_search_tags_tooltips')?>">
								<span class='glyphicon glyphicon-info-sign menu-icon'/>
							</a>
						</label>

						<?php echo form_input( array(
							'name' => 'wallpaper_search_tags',
							'value' => set_value( 'wallpaper_search_tags', show_data( @$wallpaper->wallpaper_search_tags ), false ),
							'class' => 'form-control form-control-sm',
							'placeholder' => get_msg( 'wallpaper_search_tags' ),
							'id' => 'wallpaper_search_tags'
						)); ?>

					</div>

					<div class="form-group">
						<label>
							<?php echo get_msg('credit')?>
							<a href="#" class="tooltip-ps" data-toggle="tooltip" title="<?php echo get_msg('wallpaper_search_tags_tooltips')?>">
								<span class='glyphicon glyphicon-info-sign menu-icon'/>
							</a>
						</label>

						<?php echo form_input( array(
							'name' => 'credit',
							'value' => set_value( 'credit', show_data( @$wallpaper->credit ), false ),
							'class' => 'form-control form-control-sm',
							'placeholder' => get_msg( 'credit' ),
							'id' => 'credit'
						)); ?>

					</div>

					<div class="form-group">
						<div class="form-check">

							<label class="form-check-label">
							
							<?php echo form_checkbox( array(
								'name' => 'wallpaper_is_published',
								'id' => 'wallpaper_is_published',
								'value' => 'accept',
								'checked' => set_checkbox('wallpaper_is_published', 1, ( @$wallpaper->wallpaper_is_published == 1 )? true: false ),
								'class' => 'form-check-input'
							));	?>

							<?php echo get_msg( 'wallpaper_is_pubished' ); ?>

							</label>
						</div>
					</div>

					<div class="form-group">
						<div class="form-check">

							<label class="form-check-label">
							
							<?php echo form_checkbox( array(
								'name' => 'is_recommended',
								'id' => 'is_recommended',
								'value' => 'accept',
								'checked' => set_checkbox('is_recommended', 1, ( @$wallpaper->is_recommended == 1 )? true: false ),
								'class' => 'form-check-input'
							));	?>

							<?php echo get_msg( 'wallpaper_is_recommended' ); ?>

							</label>
						</div>
					</div>

					<div class="form-group">
						<label><?php echo get_msg('wallpaper_modes_label'); ?>
							<?php 
							if ($wallpaper->is_landscape == 1) {
								echo ": Landscape";
							} else if ($wallpaper->is_portrait == 1) {
								echo ": Portrait";
							} else if ($wallpaper->is_square == 1) {
								echo ": Square";
							}
							?>
						</label>
					</div>


                </div>
                <!--  col-md-6  -->

            </div>
            <!-- /.row -->
        </div>
        <!-- /.card-body -->

        <input type="hidden" id="is_recommended_stage" name="is_recommended_stage" value="<?php echo @$wallpaper->is_recommended; ?>">

		<div class="card-footer">
            <button type="submit" value="submit" name="submit" class="btn btn-sm btn-primary">
				<?php echo get_msg('btn_save')?>
			</button>

			<a href="<?php echo $module_site_url; ?>" class="btn btn-sm btn-primary">
				<?php echo get_msg('btn_cancel')?>
			</a>
        </div>
       
    </div>
    <!-- card info -->
</section>
				

	
	

<?php echo form_close(); ?>