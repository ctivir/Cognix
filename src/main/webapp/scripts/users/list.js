$(function() {
       
     $('.openModalUsuario').on('click',function(){
         $('#modalContent').load($(this).attr('id'), function() {             
             $('#formUsuario').modal();            
        });
     });
     
    /*
    
    $(".editar").click(function(ev) {
        ev.preventDefault();
        var link = $(this).attr('href');
        openDialog(link);
    }); 
    
    $( "#dialog-form" ).dialog({
        autoOpen: false,
        modal: true,
        width: 655,
        //        height: $(window).height()-10,
        buttons: {
            "Salvar": function() {
                var d = $(this);
                $.post($("#entityUpdateForm").attr("action"), $("#entityUpdateForm").serialize()).done( function(data, status, jqxhr) {
                    if(jqxhr.status == 200) {                        
                        $( "#dialog-form" ).empty().append( data );
                    
                    }else {                        
                        $( "#dialog-form" ).empty();
                        $( d ).dialog( "close" );
                        window.location.reload();
                    }
                });
                
            },
            "Cancelar": function() {
                $( this ).empty();
                $( this ).dialog( "close" );
            }
        },
        close: function() {
            $( this ).empty();
        }
    });*/
});
