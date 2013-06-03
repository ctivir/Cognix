$(function() {
    /*
    $("#uploader").pluploadQueue({
	        // General settings
	        runtimes : 'html5,gears,flash,silverlight,browserplus',
                url : $("#formUpload").attr("action"),

                max_file_size : '10mb',
	        chunk_size : '1mb',
	        unique_names : true,
	 
	        // Resize images on clientside if we can
	        resize : {width : 320, height : 240, quality : 90},
	 
	        // Specify what files to browse for
	        filters : [
	            {title : "Image files", extensions : "jpg,gif,png"},
	            {title : "Zip files", extensions : "zip"}
	        ],
	 
	        // Flash settings
                flash_swf_url : window.urlRoot+'/scripts/upload/plupload/plupload.flash.swf',
	 
	        // Silverlight settings
                silverlight_xap_url : window.urlRoot+'/scripts/upload/plupload/plupload.silverlight.xap'
	    });
    */

    $("#uploader").plupload({
        // General settings
        runtimes : 'gears,flash,browserplus,silverlight,html5,html4',
        url : $("#formUpload").attr("action"),
        max_file_size : '1024mb',
        chunk_size : '1mb',
        unique_names : true,
        drop_element : 'uploader',

        // Specify what files to browse for
        filters : [
        {
            title : "Librejo files", 
            extensions : "pdf, mkv,xvid,webm,avi,avi,mpg,mov,wmv,avchd,3gp,asf,m4v,mpeg,mpeg4,mpg4,mp4"
        }
        
        ],

        // Flash settings
        flash_swf_url : window.urlRoot+'/scripts/upload/plupload/plupload.flash.swf',

        // Silverlight settings
        silverlight_xap_url : window.urlRoot+'/scripts/upload/plupload/plupload.silverlight.xap',
        multipart_params: {
            'user': 'marcos', 
            'time': '2013-03-13'
        }
    });
   

    // Client side form validation
    $("#formUpload").submit(function(e) {
        var uploader = $('#uploader').plupload('getUploader');

        // Files in queue upload them first
        if (uploader.files.length > 0) {     
            // When all files are uploaded submit form
            uploader.bind('StateChanged', function() {
                if (uploader.files.length === (uploader.total.uploaded + uploader.total.failed)) {
                    $('form')[0].submit();
                }
            });
                
            uploader.start();
        } else
            alert('You must at least upload one file.');

        return false;
    });
    
});