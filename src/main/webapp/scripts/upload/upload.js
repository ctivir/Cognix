$(function() {
    $("#uploader").pluploadQueue({
        // General settings
        runtimes: 'gears,flash,browserplus,silverlight,html5',
        url: window.urlRoot + 'files/uploadFile',
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
        init: {
            FileUploaded: function(up) {
                $('#validation_locate_file').val('file');

                if (this.total.queued == 0) {
                    $.ajax({
                        type: "POST",
                        url: window.urlRoot + "documents/new/generateMetadata",
                        data: {
                            id: docId
                        },
                        success: function(result) {
                            // Suggestions based on the files
                            makeSuggestions(result);
                        },
                        error: function() {
                            alert("error");
                        },
                        datatype: "json"
                    });
                }
            },
            BeforeUpload: function(up, file) {
                //send the file name to controller
                up.settings.multipart_params["filename"] = file.name;
            }
        }
    });

    var makeSuggestions = function(suggestions) {

        var lang = window.navigator.userLanguage || window.navigator.language;
        
        //General
        $('#titulo').val(suggestions.title);
        $('#language').val(suggestions.language);
        $('#structure select').val(suggestions.structure);
        $('#aggregationLevel input[value=' + suggestions.aggregationLevel + ']').prop('checked', true);
        $('#language').val(lang);

        //Educational        
            
            //slider value
        $('#interactivityLevel select').val(suggestions.interactivityLevel);
        $('#interactivityLevelSlider').slider("value", $('#interactivityLevel select')[0].selectedIndex);
        
            //lang value
        $('#eduLanguage').val(lang);
        
            //regular select value
        $('#perception select').val(suggestions.perception);        
        $('#interactivityType select').val(suggestions.interactivityType);
        
            //radio option
        $('#synchronism input[value=' + suggestions.synchronism + ']').prop('checked', true);
        $('#coPresence input[value=' + suggestions.copresense + ']').prop('checked', true);
        $('#reciprocity input[value=' + suggestions.reciprocity + ']').prop('checked', true);
        
        //Accessibility
        $('#hasVisual input[value=' + suggestions.visual + ']').prop('checked', true);
        $('#hasAuditory input[value=' + suggestions.auditory + ']').prop('checked', true);
        $('#hasTactile input[value=' + suggestions.tactil + ']').prop('checked', true);
        

        
    };
});
