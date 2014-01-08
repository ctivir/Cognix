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
        var obj = $('#titulo');
        if (obj.val() === "") {
            obj.val(suggestions.title).addClass('suggestions');
        }
        obj = $('#language');
        if (obj.val() === "") {
            obj.val(suggestions.language).addClass('suggestions');
        }
        obj = $('#structure select');
        if (obj.val() === "") {
            obj.val(suggestions.structure).addClass('suggestions');
        }
        if ($('#aggregationLevel input:last').prop('checked')) {
            $('#aggregationLevel input[value=' + suggestions.aggregationLevel + ']').prop('checked', true);
        }

        obj = $('#language');
        if (obj.val() === "") {
            obj.val(lang).addClass('suggestions');
        }

        //Educational        

        //slider value
        obj = $('#interactivityLevel select');
        if (obj.val() === "") {
            obj.val(suggestions.interactivityLevel);
            $('#interactivityLevelSlider').slider("value", $('#interactivityLevel select')[0].selectedIndex);
        }

        //lang value
        obj = $('#eduLanguage');
        if (obj.val() === "") {
            obj.val(lang).addClass('suggestions');
        }

        //regular select value
        obj = $('#perception select');
        if (obj.val() === "") {
            obj.val(suggestions.perception).addClass('suggestions');
        }

        obj = $('#interactivityType select');
        if (obj.val() === "") {
            obj.val(suggestions.interactivityType).addClass('suggestions');
        }

        //radio option
        if ($('#synchronism input:last').prop('checked')) {
            $('#synchronism input[value=' + suggestions.synchronism + ']').prop('checked', true);
            $('#synchronism').children().last().addClass('suggestions');
        }

        if ($('#coPresence input:last').prop('checked')) {
            $('#coPresence input[value=' + suggestions.copresense + ']').prop('checked', true);
            $('#coPresence').children().last().addClass('suggestions');
        }
        if ($('#reciprocity input:last').prop('checked')) {
            $('#reciprocity input[value=' + suggestions.reciprocity + ']').prop('checked', true);
            $('#reciprocity').children().last().addClass('suggestions');
        }

        //Accessibility
        if ($('#hasVisual input:last').prop('checked')) {
            $('#hasVisual input[value=' + suggestions.visual + ']').prop('checked', true);
            $('#hasVisual').children().last().addClass('suggestions');
        }
        if ($('#hasAuditory input:last').prop('checked')) {
            $('#hasAuditory input[value=' + suggestions.auditory + ']').prop('checked', true);
            $('#hasAuditory').children().last().addClass('suggestions');
        }
        if ($('#hasTactile input:last').prop('checked')) {
            $('#hasTactile input[value=' + suggestions.tactil + ']').prop('checked', true);
            $('#hasTactile').children().last().addClass('suggestions');
        }
        //Technical   
        obj = $('#type input');
        if (obj.val() === "") {
            obj.val(suggestions.requirementsType).addClass('suggestions');
        }

        obj = $('#name input');
        if (obj.val() === "") {
            obj.val(suggestions.requirementsName).addClass('suggestions');
        }
        console.log(suggestions.supportedPlatforms);
        //Checkbox options            
        for (var i = 0; i < suggestions.supportedPlatforms.length; i++) {
            $('#supportedPlatforms input[value=' + suggestions.supportedPlatforms[i] + ']').prop('checked', true).addClass('suggestions');
        }

        obj = $('#otherPlatformRequirements input');
        if (obj.val() === "") {
            obj.val(suggestions.otherPlatformRequirements).addClass('suggestions');
        }
        
        //After all suggestions change their classes when 
        $('.suggestions').focus(function() {
            //TODO: se for um checkbox remover do div superior.
            $(this).removeClass('suggestions');
        });
    };
});
