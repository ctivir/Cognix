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
        obj = $('#structure select');
        if (obj.val() === "" && suggestions.structure !== "") {
            obj.val(suggestions.structure).addClass('suggestions');
        }
        if ($('#aggregationLevel input:last').prop('checked')) {
            $('#aggregationLevel input[value=' + suggestions.aggregationLevel + ']').prop('checked', true);
        }

        obj = $('#language');
        if (obj.val() === "" && lang !== "") {
            obj.val(lang).addClass('suggestions');
        }

        //Educational        

        //slider value
        obj = $('#interactivityLevel select');
        if (obj.val() === "" && suggestions.interactivityLevel!=="") {
            obj.val(suggestions.interactivityLevel);
            $('#interactivityLevelSlider').slider("value", $('#interactivityLevel select')[0].selectedIndex);
            $('#interactivityLevel select').addClass('suggestions');
        }

        //lang value
        obj = $('#eduLanguage');
        if (obj.val() === "" && lang !== "") {
            obj.val(lang).addClass('suggestions');
        }

        //regular select value
        obj = $('#perception select');
        if (obj.val() === "" && suggestions.perception !== "") {
            obj.val(suggestions.perception).addClass('suggestions');
        }

        obj = $('#interactivityType select');
        if (obj.val() === "" && suggestions.interactivityType !=="") {
            obj.val(suggestions.interactivityType).addClass('suggestions');
        }

        //radio option
        if ($('#synchronism input:last').prop('checked') && suggestions.synchronism!=="") {
            $('#synchronism input[value=' + suggestions.synchronism + ']').prop('checked', true);
            $('#synchronism').children().last().addClass('suggestions ui-corner-all');
        }

        if ($('#coPresence input:last').prop('checked') && suggestions.copresense!=="") {
            $('#coPresence input[value=' + suggestions.copresense + ']').prop('checked', true);
            $('#coPresence').children().last().addClass('suggestions ui-corner-all');
        }
        if ($('#reciprocity input:last').prop('checked') && suggestions.reciprocity!=="") {
            $('#reciprocity input[value=' + suggestions.reciprocity + ']').prop('checked', true);
            $('#reciprocity').children().last().addClass('suggestions ui-corner-all');
        }

        //Accessibility
        if ($('#hasVisual input:last').prop('checked') && suggestions.visual !== "") {
            $('#hasVisual input[value=' + suggestions.visual + ']').prop('checked', true);
            $('#hasVisual').children().last().addClass('suggestions ui-corner-all');
        }        
        if ($('#hasAuditory input:last').prop('checked') && suggestions.auditory !== "") {
            $('#hasAuditory input[value=' + suggestions.auditory + ']').prop('checked', true);
            $('#hasAuditory').children().last().addClass('suggestions ui-corner-all');
        }
        if ($('#hasText input:last').prop('checked')) {
            $('#hasText input[value=' + suggestions.textual + ']').prop('checked', true);
            $('#hasText').children().last().addClass('suggestions ui-corner-all');
        }
        if ($('#hasTactile input:last').prop('checked') && suggestions.tactil !== "") {
            $('#hasTactile input[value=' + suggestions.tactil + ']').prop('checked', true);
            $('#hasTactile').children().last().addClass('suggestions ui-corner-all');
        }
        
        //Technical   
        obj = $('#type select');
        if (obj.val() === "" && suggestions.requirementsType !== "") {
            obj.val(suggestions.requirementsType).addClass('suggestions');
        }

        obj = $('#name select');
        if (obj.val() === "" && suggestions.requirementsName !== "") {
            obj.val(suggestions.requirementsName).addClass('suggestions');
        }
        //Checkbox options            
        for (var i = 0; i < suggestions.supportedPlatforms.length; i++) {
            $('#supportedPlatforms input[value=' + suggestions.supportedPlatforms[i] + ']').prop('checked', true);
            $('#supportedPlatforms').children().last().addClass('suggestions ui-corner-all');
        }

        obj = $('#otherPlatformRequirements input');
        if (obj.val() === "" && suggestions.otherPlatformRequirements !== "") {
            obj.val(suggestions.otherPlatformRequirements).addClass('suggestions');
        }
        
        //After all suggestions change their classes when 
        $('.suggestions').focus(function() {
            //TODO: se for um checkbox remover do div superior.
            $(this).removeClass('suggestions');
        });
        
        $(':checkbox').focus(function() {
            $(this).parents(".suggestions").removeClass("suggestions");
        });
        
        $(':radio').focus(function() {
            $(this).parents(".suggestions").removeClass("suggestions");
        });
    };
});
