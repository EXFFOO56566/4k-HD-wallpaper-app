
<?php
	$attributes = array( 'id' => 'shuffle-form', 'enctype' => 'multipart/form-data');
	echo form_open( '', $attributes);
?>
	
<section class="content animated fadeInRight">
	<div class="col-md-12">
		<div class="card card-info">
		    <div class="card-header">
		        <h3 class="card-title"><?php echo get_msg('shuffle_info')?></h3>
		    </div>
	        <!-- /.card-header -->
	        <div class="card-body">
	            <div class="row">
	                <div class="col-md-2">
						<div class="form-group">
							<label><input type="radio" name="status" value="daily" <?php 
					       		$daily = $shuffle->status;
					        if ($daily == "daily") echo "checked"; ?> >
					          Daily </label>
						</div>
					</div>
					<div class="col-md-2">
						<div class="form-group">
							<label><input type="radio" name="status" value="monthly" <?php 
					       		$monthly = $shuffle->status;
					        if ($monthly == "monthly") echo "checked"; ?> >
					          Monthly </label>
						</div>
					</div>
					<div class="col-md-2">
						<div class="form-group">
							<label><input type="radio" name="status" value="yearly" <?php 
					       		$yearly = $shuffle->status;
					        if ($yearly == "yearly") echo "checked"; ?> >
					          Yearly </label>
						</div>
					</div>
					<div class="col-md-2">
						<div class="form-group">
							<label><input type="radio" name="status" value="manaul" <?php 
					       		$manaul = $shuffle->status;
					        if ($manaul == "manaul") echo "checked"; ?> >
					          Manaul </label>
						</div>
					</div>
					<div class="col-md-2">
						<div class="form-group">
							<label><input type="radio" name="status" value="no" <?php 
					       		$no = $shuffle->status;
					        if ($no == "no") echo "checked"; ?> >
					         No </label>
						</div>
					</div>
	            </div>
	                <!-- col-md-6 -->
	        </div>
	            <!-- /.row -->
        	<!-- /.card-body -->

			<div class="card-footer">
	            <button type="submit" class="btn btn-sm btn-primary">
					<?php echo get_msg('btn_save')?>
				</button>

				<a href="<?php echo $module_site_url; ?>" class="btn btn-sm btn-primary">
					<?php echo get_msg('btn_cancel')?>
				</a>
	        </div>
	    </div>
	</div>
    
    <!-- card info -->
</section>
				
<?php echo form_close(); ?>