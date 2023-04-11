
<?php
	$attributes = array('id' => 'user-form');
	echo form_open( '', $attributes );
?>
	<h5><?php echo get_msg('user_info')?></h5>

	<div id="perm_err" class="alert alert-danger fade in" style="display: none">
		<label for="permissions[]" class="error"></label>
		<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
	</div>
		
	<div class="row animated fadeInRight">
		<div class="col-6">
				<div class="form-group">
					<label><?php echo get_msg('user_name')?></label>

					<?php echo form_input(array(
						'name' => 'user_name',
						'value' => set_value( 'user_name', show_data( @$user->user_name ), false ),
						'class' => 'form-control form-control-sm',
						'placeholder' => 'Name',
						'id' => 'name'
					)); ?>

				</div>
				
				<div class="form-group">
					<label><?php echo get_msg('user_email')?></label>

					<?php echo form_input(array(
						'name' => 'user_email',
						'value' => set_value( 'user_email', show_data( @$user->user_email ), false ),
						'class' => 'form-control form-control-sm',
						'placeholder' => 'Email',
						'id' => 'user_email'
					)); ?>

				</div>
				
				<?php if ( @$user->user_is_sys_admin == false ): ?>

				<div class="form-group">
					<label><?php echo get_msg('user_password')?></label>

					<?php echo form_input(array(
						'type' => 'password',
						'name' => 'user_password',
						'value' => set_value( 'user_password' ),
						'class' => 'form-control form-control-sm',
						'placeholder' => 'Password',
						'id' => 'user_password'
					)); ?>
				</div>
							
				<div class="form-group">
					<label><?php echo get_msg('conf_password')?></label>
					
					<?php echo form_input(array(
						'type' => 'password',
						'name' => 'conf_password',
						'value' => set_value( 'conf_password' ),
						'class' => 'form-control form-control-sm',
						'placeholder' => 'Conf Password',
						'id' => 'conf_password'
					)); ?>
				</div>
				
				<div class="form-group">
					<label><?php echo get_msg('role')?></label>
					<select class="form-control form-control-sm" name='role_id' id='role_id'>
						<?php
							foreach($this->Role->get_all()->result() as $role)
								echo "<option value='".$role->role_id."'>".$role->role_desc."</option>";
						?>
					</select>
				</div>

				<?php endif; ?>
		</div>
		
		<?php if ( @$user->user_is_sys_admin == false ): ?>

		<div class="col-6">
			<div class="form-group">
				<label><?php echo get_msg('allowed_modules')?></label>
				
				<?php foreach($this->Module->get_all()->result() as $module): ?>

					<div class="form-check">
						<label class="form-check-label">
						
						<?php echo form_checkbox('permissions[]', $module->module_id, set_checkbox('permissions', $module->module_id, $this->User->has_permission( $module->module_name, @$user->user_id ))); ?>

						<?php echo $module->module_desc; ?>

						</label>
					</div>

				<?php endforeach; ?>
			
				</label>
			</div>
		</div>

		<?php endif; ?>

	</div>
	
	<div class="my-3">
		<button type="submit" class="btn btn-sm btn-primary"><?php echo get_msg('btn_save')?></button>
		<a href="<?php echo $module_site_url; ?>" class="btn btn-sm btn-primary"><?php echo get_msg('btn_cancel')?></a>
	</div>

<?php echo form_close(); ?>