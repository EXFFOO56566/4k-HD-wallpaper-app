<div class="table-responsive animated fadeInRight">
	<table class="table m-0 table-striped">
		<tr>
			<th><?php echo get_msg('no'); ?></th>
			<th><?php echo get_msg('wallpaper_name'); ?></th>
			<th><?php echo get_msg('wallpaper_cat'); ?></th>
			<th><?php echo get_msg('wallpaper_img'); ?></th>
			<th><?php echo get_msg('free_premium'); ?></th>
			<th><?php echo get_msg('wallpaper_point'); ?></th>
			<th><?php echo get_msg('uploaded_user'); ?></th>
			
			<?php if ( $this->ps_auth->has_access( EDIT )): ?>
				
				<th><?php echo get_msg('btn_edit')?></th>
			
			<?php endif; ?>
			
			<?php if ( $this->ps_auth->has_access( DEL )): ?>
				
				<th><?php echo get_msg('btn_delete')?></th>
			
			<?php endif; ?>
			
			<?php if ( $this->ps_auth->has_access( PUBLISH )): ?>
				
				<th><?php echo get_msg('btn_publish')?></th>
			
			<?php endif; ?>

		</tr>
		
		<?php $count = $this->uri->segment(4) or $count = 0; ?>

		<?php if ( !empty( $wallpapers ) && count( $wallpapers->result()) > 0 ): ?>

			<?php foreach($wallpapers->result() as $wallpaper): ?>
				
				<tr>
					<td><?php echo ++$count;?></td>
					<td><?php echo $wallpaper->wallpaper_name;?></td>
					<td><?php echo $this->Category->get_one($wallpaper->cat_id)->cat_name;?></td>

					<?php 

						$default_photo = get_default_photo( $wallpaper->wallpaper_id, 'wallpaper' );
						$default_video_icon = get_default_photo( $wallpaper->wallpaper_id, 'video-icon' );

					?>	
					
					<?php 
						$photo_width = $default_photo->img_width;
						$photo_height = $default_photo->img_height;
						$icon_width = $default_video_icon->img_width;
						$icon_height = $default_video_icon->img_height;
						$width = "";
						$height = "";
						if ( ($photo_width > $photo_height) || ($icon_width > $icon_height) ) {
							$width = "150px";
							$height = "100px";
						} elseif ( ($photo_width < $photo_height) || ($icon_width < $icon_height) ) {
							$width = "80px";
							$height = "100px";
						} else {
							$width = "100px";
							$height = "100px";
						}
					?>
					<?php if ($wallpaper->is_gif == 1) { ?>
						
						<td><img style="width: <?php echo $width ?>;height: <?php echo $height ?>;" src="<?php echo img_url( '/'. $default_photo->img_path ); ?>"/></td>

					<?php } elseif ($wallpaper->is_video_wallpaper == 1) { ?>
						
						<td><img style="width: <?php echo $width ?>;height: <?php echo $height ?>;" src="<?php echo img_url( '/thumbnail/'. $default_video_icon->img_path ); ?>"/></td>
						

					<?php } else{ ?>

						<td><img style="width: <?php echo $width ?>;height: <?php echo $height ?>;" src="<?php echo img_url( '/thumbnail/'. $default_photo->img_path ); ?>"/></td>
						

					<?php } ?>
					
					<td>
					
						<?php if ( $wallpaper->types == 1 ): ?>
							<button class="btn btn-default" style="font-weight: bold;background-color: #4cceac;width: 80px;">Free</button>
						<?php else:?>
							<button class="btn btn-default" style="font-weight: bold;background-color: #f49389;width: 80px;">Premium</button>
						<?php endif;?>
					</td>

					<td>
						<?php echo $wallpaper->point; ?>
					</td>

					<td>
						<?php echo $this->User->get_one($wallpaper->added_user_id)->user_name; ?>
					</td>

					<?php if ( $this->ps_auth->has_access( EDIT )): ?>
				
						<td>
							<a href='<?php echo $module_site_url .'/edit/'. $wallpaper->wallpaper_id; ?>'>
								<i style='font-size: 18px;' class='fa fa-pencil-square-o'></i>
							</a>
						</td>
					
					<?php endif; ?>
					
					<?php if ( $this->ps_auth->has_access( DEL )): ?>
						
						<td>
							<a herf='#' class='btn-delete' data-toggle="modal" data-target="#myModal" id="<?php echo $wallpaper->wallpaper_id;?>">
								<i style='font-size: 18px;' class='fa fa-trash-o'></i>
							</a>
						</td>
					
					<?php endif; ?>
					
					<?php if ( $this->ps_auth->has_access( PUBLISH )): ?>
						
						<td>
							<?php if ( @$wallpaper->wallpaper_is_published == 1): ?>
								<button class="btn btn-sm btn-success unpublish" wallpaperid='<?php echo $wallpaper->wallpaper_id;?>'>
								Yes</button>
							<?php else:?>
								<button class="btn btn-sm btn-danger publish" wallpaperid='<?php echo $wallpaper->wallpaper_id;?>'>
								No</button><?php endif;?>
						</td>
					
					<?php endif; ?>

				</tr>

			<?php endforeach; ?>

		<?php else: ?>
				
			<?php $this->load->view( $template_path .'/partials/no_data' ); ?>

		<?php endif; ?>

	</table>
</div>