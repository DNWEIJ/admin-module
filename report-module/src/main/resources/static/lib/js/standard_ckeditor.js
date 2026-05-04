import {
    addListToDropdown,
    Alignment,
    Base64UploadAdapter,
    BlockQuote,
    Bold,
    ClassicEditor,
    Collection,
    Command,
    createDropdown,
    createLabeledInputText,
    Dialog,
    Essentials,
    Font,
    Heading,
    HorizontalLine,
    Image,
    ImageCaption,
    ImageInsert,
    ImageResize,
    ImageStyle,
    ImageToolbar,
    Indent,
    Italic,
    LabeledFieldView,
    Link,
    List,
    Paragraph,
    Plugin,
    RemoveFormat,
    SourceEditing,
    SpecialCharacters,
    SpecialCharactersEssentials,
    Strikethrough,
    Table,
    TableToolbar,
    toWidget,
    Underline,
    Undo,
    View,
    Widget
} from 'ckeditor5';

// ===== EDITOR INITIALIZATION =====
function initWhenReady() {
    const editorElement = document.querySelector('#editor');
    if (!editorElement) {
        setTimeout(initWhenReady, 50);
        return;
    }

    ClassicEditor.create(editorElement, {
        plugins: [
            Essentials, Bold, Italic, Underline, Strikethrough, Font, Paragraph, Heading, List, Link, BlockQuote, Table, TableToolbar,
            Alignment, Indent, HorizontalLine, SpecialCharacters, SpecialCharactersEssentials, RemoveFormat, Undo, SourceEditing,
            Image, ImageToolbar, ImageCaption, ImageStyle, ImageResize, ImageInsert, Base64UploadAdapter
        ],
        toolbar: {
            items: [
                'heading', '|', 'fontSize', 'fontFamily', 'fontColor', 'fontBackgroundColor', '|', 'bold', 'italic', 'underline', 'strikethrough', '|',
                'alignment', '|', 'numberedList', 'bulletedList', '|', 'outdent', 'indent', '|', 'link', 'insertImage', 'insertTable', 'blockQuote', '|', 'horizontalLine', 'specialCharacters', '|',
                'removeFormat', '|', 'undo', 'redo', '|', 'sourceEditing'
            ],
            shouldNotGroupWhenFull: true
        },
        image: {
            toolbar: [
                'imageStyle:inline',
                'imageStyle:block',
                'imageStyle:side',
                '|',
                'toggleImageCaption',
                'imageTextAlternative'
            ]
        },
        table: {
            contentToolbar: ['tableColumn', 'tableRow', 'mergeTableCells']
        }
    })
        .then(editor => {
            window.editor = editor;
            console.log('✅ CKEditor ready');
        })
        .catch(error => {
            console.error('CKEditor error:', error);
        });
}

if (document.readyState === 'loading') {
    initWhenReady();
} else {
    initWhenReady();
}