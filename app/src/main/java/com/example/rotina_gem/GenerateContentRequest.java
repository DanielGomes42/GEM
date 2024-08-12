package com.example.rotina_gem;

import java.util.List;

public class GenerateContentRequest {
    private List<Content> contents;

    public GenerateContentRequest(List<Content> contents) {
        this.contents = contents;
    }

    public static class Content {
        private List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }

        public static class Part {
            private String text;

            public Part(String text) {
                this.text = text;
            }
        }
    }
}

