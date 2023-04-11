 <!-- TABLE: LATEST ORDERS -->
<div class="card-header border-transparent">
  <h3 class="card-title">
    <span class="badge badge-warning" style="height: 30px; padding: 10px; font-size: 14px; ">

      <?php echo get_msg('total_label'); ?>
      <?php echo get_msg('divider_label'); ?>
      <?php echo $total_count; ?>
      <?php echo get_msg('wallpapers_label'); ?>

    </span>
  </h3>

  <div class="card-tools">

    <button type="button" class="btn btn-tool" data-widget="collapse">
      <i class="fa fa-minus"></i>
    </button>
    <button type="button" class="btn btn-tool" data-widget="remove">
      <i class="fa fa-times"></i>
    </button>

  </div>
</div>
<!-- /.card-header -->
<div class="card-body table-responsive p-0">
  
  <table class="table m-0 table-striped">
    <tr>
      <th><?php echo get_msg('no'); ?></th>
      <th><?php echo get_msg('wallpaper_name'); ?></th>
      <th><?php echo get_msg('wallpaper_cat'); ?></th>
      <th><?php echo get_msg('wallpaper_img'); ?></th>
      <th><?php echo get_msg('wallpaper_date'); ?></th>
    </tr>
    
    <?php $count = $this->uri->segment(4) or $count = 0; ?>
    <?php if ( ! empty( $data )): ?>
      <?php foreach($data as $d): ?>
          <?php $wallpaper = get_default_photo( $d->wallpaper_id, 'wallpaper' ); ?>
          <?php $wallpaper_count = $this->Wallpaper->count_all_by(array("cat_id" => $d->cat_id)); ?>
          <tr>
            <td><?php echo ++$count; ?></td>
            <td><?php echo $d->wallpaper_name; ?></td>
            <td><?php echo $this->Category->get_one($d->cat_id)->cat_name;?></td>
            <td>

              <div class="crop">
                <img src="<?php echo img_url( $wallpaper->img_path ); ?>">
              </div>
            </td>
            <td>
              <div class="sparkbar" data-color="#00a65a" data-height="20"><?php echo $d->added_date; ?></div>
            </td>
          </tr>
        <?php endforeach; ?>
      <?php endif; ?>
   
  </table>
</div>
  <!-- /.table-responsive -->

<div class="card-footer text-center">
  <a href="<?php echo site_url('admin/wallpapers'); ?>"><?php echo get_msg('view_all_label'); ?></a>
</div>
<!-- /.card-footer -->
           