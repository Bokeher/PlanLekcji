package com.example.planlekcji.ckziu_elektryk.client.stubs;

public final class TestConstants {

    public static final String URL = "http://localhost:8000/api/v1";
    public static final String TOKEN = "token";
    public static final  String RESPONSE_REPLACEMENTS_PERIOD = """
                        {
                            "2026-09-07": [
                                {
                                    "name": "Karsnal Maciej",
                                    "changes": [
                                        {
                                            "period": "Cały dzień",
                                            "info": "Nieobecność"
                                        }
                                    ]
                                },
                            ],
                            "2026-09-08": [
                                {
                                    "name": "Jarwor Paweł",
                                    "changes": [
                                        {
                                            "period": "Cały dzień",
                                            "info": "Nieobecność"
                                        }
                                    ]
                                },
                            ],
                            "2026-09-09": [
                                {
                                    "name": "Kowslska Agata",
                                    "changes": [
                                        {
                                            "period": "Cały dzień",
                                            "info": "Nieobecność"
                                        }
                                    ]
                                },
                            ],
                            "2026-09-10": [
                                {
                                    "name": "Nowak Wojciech",
                                    "changes": [
                                        {
                                            "period": "1 - 4",
                                            "info": "Nieobecność"
                                        }
                                    ]
                                },
                            ],
                            "2026-09-11": [
                                {
                                    "name": "Anioł Aniela",
                                    "changes": [
                                        {
                                            "period": "Cały dzień",
                                            "info": "Nieobecność"
                                        }
                                    ]
                                },
                            ]
                        }
                        """;
}
