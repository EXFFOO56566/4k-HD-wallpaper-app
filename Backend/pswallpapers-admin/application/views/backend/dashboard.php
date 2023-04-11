 <section class="content animated fadeInRight">
  <!-- Content Header (Page header) -->
  <div class="content-header">
      <div class="container-fluid">
        <div class="row mb-2">
          <div class="col-sm-6">
            <h1 class="m-0 text-dark"> Welcome, <?php echo $this->ps_auth->get_user_info()->user_name;?>!</h1>
            <?php flash_msg(); ?>
          </div><!-- /.col -->
        </div><!-- /.row -->
      </div><!-- /.container-fluid -->
  </div>
  <!-- /.content-header -->

  <!-- Main content -->
    
  <div class="container-fluid">
    <div class="card-body">
      <div class="row"> 
        <div class="col-lg-3 col-6">
          <!-- small box -->
            <?php 
              $data = array(
                'url' => site_url() . "/admin/wallpapers" ,
                'total_count' => $this->Wallpaper->count_all(),
                'label' => get_msg( 'total_wallpaper_count_label'),
                'icon' => "ion-images",
                'color' => "bg-primary"
              );

              $this->load->view( $template_path .'/components/badge_count', $data );
            ?>
        </div>

        <div class="col-lg-3 col-6">
            <!-- small box -->
            <?php 
              $conds['role_id'] = 4;
              $data = array(
                'url' => site_url() . "/admin/system_users" ,
                'total_count' => $this->User->count_all_by($conds),
                'label' => get_msg( 'total_users_count_label'),
                'icon' => "fa fa-group",
                'color' => "bg-success"
              );

              $this->load->view( $template_path .'/components/badge_count', $data ); 
            ?>
        </div>

        <!-- ./col -->
        <div class="col-lg-3 col-6">
          <!-- small box -->
          <?php 
            $data = array(
              'url' => site_url() . "/admin/categories" ,
              'total_count' => $this->Category->count_all(),
              'label' => get_msg( 'total_cat_count_label'),
              'icon' => "ion ion-stats-bars",
              'color' => "bg-info"
            );

            $this->load->view( $template_path .'/components/badge_count', $data ); 
          ?>
        </div>
        <!-- ./col -->

        <!-- ./col -->
        <div class="col-lg-3 col-6">
          <!-- small box -->
          <?php 
            $data = array(
              'url' => site_url() . "/admin/contacts" ,
              'total_count' => $this->Contact->count_all(),
              'label' => get_msg( 'total_contact_label'),
              'icon' => "icon ion-chatbox",
              'color' => "bg-danger"
            );

            $this->load->view( $template_path .'/components/badge_count', $data );
          ?>
        </div>
        <!-- ./col -->

        <div class="col-md-6">
          <div class="card">
            <?php 

              $data = array(
                'url' => site_url() . "/admin/touches" ,
                'panel_title' => get_msg('wallpaper_touch_title'),
                'module_name' => 'touches' ,
                'total_count' => $this->Touch->count_all(),
                'data' => $this->Touch->get_wallpaper_count(5)->result()
              );

              $this->load->view( $template_path .'/components/wallpaper_popular_panel', $data ); 
            ?>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card">
           <?php

              $conds['role_id'] = 4;
              $data = array(
                'panel_title' => get_msg('user_latest_members'),
                'module_name' => 'wallpapers' ,
                'total_count' => $this->User->count_all_by($conds),
                'data' => $this->User->get_all_by($conds,4)->result()
              );

              $this->load->view( $template_path .'/components/summary_user_panel', $data ); 
            ?>
          </div>
        </div>

        <div class="col-12">
          <div class="card">
            <?php
              $conds['no_publish_filter'] = 1;
              $conds['order_by'] = 1;
              $conds['order_by_field'] = "added_date";
              $conds['order_by_type'] = "desc";


              $data = array(
                'panel_title' => get_msg('wallpaper_panel_title'),
                'module_name' => 'wallpapers' ,
                'total_count' => $this->Wallpaper->count_all_by($conds),
                'data' => $this->Wallpaper->get_all_by($conds,4)->result()
              );

              $this->load->view( $template_path .'/components/summary_wallpaper_panel', $data ); 
            ?>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card">
            <?php 
              $conds['no_publish_filter'] = 1;
              $conds['order_by'] = 1;
              $conds['order_by_field'] = "added_date";
              $conds['order_by_type'] = "desc";

              $data = array(
                'panel_title' => get_msg('category_panel_title'),
                'module_name' => 'categories' ,
                'total_count' => $this->Category->count_all_by($conds),
                'data' => $this->Category->get_all_by($conds,4)->result()
              );

              $this->load->view( $template_path .'/components/summary_category_panel', $data ); 
            ?>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card">
            <?php
              $data = array(
                'panel_title' => get_msg('contact_message'),
                'module_name' => 'wallpapers' ,
                'total_count' => $this->Contact->count_all(),
                'data' => $this->Contact->get_all(2)->result()
              );

              $this->load->view( $template_path .'/components/summary_contact_panel', $data ); 
            ?>
          </div>
        </div>
        <!-- col -->
      </div>
    </div>
       
  </section>