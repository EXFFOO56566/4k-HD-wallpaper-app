<script>
	$(document).ready(function(){
		$('#gcm-form').validate({
			rules:{
				message:{
					required: true,
					minlength: 1
				}
			},
			messages:{
				message:{
					required: "Please Fill Push Message.",
					minlength: "The length of message must be greater than 1"
				}
			}
		});
	});

	$(function () { $("[data-toggle='tooltip']").tooltip(); });
</script>