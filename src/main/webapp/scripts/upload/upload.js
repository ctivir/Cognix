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
                            alert("Sem sugestões para preenchimento.");
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
        $('#titulo').val(suggestions.title).addClass('suggestions');
        $('#language').val(suggestions.language).addClass('suggestions');
        $('#structure select').val(suggestions.structure).addClass('suggestions');
        $('#aggregationLevel input[value=' + suggestions.aggregationLevel + ']').prop('checked', true);
        $('#language').val(lang).addClass('suggestions');

        //Educational        

            //slider value
        $('#interactivityLevel select').val(suggestions.interactivityLevel);
        $('#interactivityLevelSlider').slider("value", $('#interactivityLevel select')[0].selectedIndex);

            //lang value
        $('#eduLanguage').val(lang).addClass('suggestions');

            //regular select value
        $('#perception select').val(suggestions.perception);
        $('#interactivityType select').val(suggestions.interactivityType).addClass('suggestions');

            //radio option
        $('#synchronism input[value=' + suggestions.synchronism + ']').prop('checked', true).addClass('suggestions');
        $('#coPresence input[value=' + suggestions.copresense + ']').prop('checked', true).addClass('suggestions');
        $('#reciprocity input[value=' + suggestions.reciprocity + ']').prop('checked', true).addClass('suggestions');

        //Accessibility
        $('#hasVisual input[value=' + suggestions.visual + ']').prop('checked', true).addClass('suggestions');
        $('#hasAuditory input[value=' + suggestions.auditory + ']').prop('checked', true).addClass('suggestions');
        $('#hasTactile input[value=' + suggestions.tactil + ']').prop('checked', true).addClass('suggestions');

        //Technical        
        $('#type input').val(suggestions.requirementsType).addClass('suggestions');
        $('#name input').val(suggestions.requirementsName).addClass('suggestions');

        //Checkbox options            
        for (var i = 0; i < suggestions.supportedPlatforms.length; i++) {
            $('#supportedPlatforms input[value=' + suggestions.supportedPlatforms[i] + ']').prop('checked', true).addClass('suggestions');
        }

        $('#otherPlatformRequirements input').val(suggestions.otherPlatformRequirements).addClass('suggestions');

        //After all suggestions change their classes when 
        $('.suggestions').focus(function() {            
            $(this).removeClass('suggestions');            
        });        
    };
});
