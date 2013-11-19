$(function() {       
    $("#uploader").pluploadQueue({
        // General settings
        runtimes : 'html5,gears,flash,browserplus,silverlight',
        url: window.urlRoot+'files/uploadFile',
        max_file_size: '1024mb',
        chunk_size: '1mb',
        unique_names: true,
        drop_element: 'uploader',
        // Flash settings
        flash_swf_url: window.urlRoot + '/scripts/upload/plupload/plupload.flash.swf',
        // Silverlight settings
        silverlight_xap_url: window.urlRoot + '/scripts/upload/plupload/plupload.silverlight.xap',
        multipart_params: {
            'docId': docId
        },
        // Post init events, bound after the internal events
        init : {
            FileUploaded: function(up) {
                alert('teste');
            },
            BeforeUpload: function(up, file) {
                //send the file name to controller
                up.settings.multipart_params["filename"] = file.name;
            }
        }
    });
});