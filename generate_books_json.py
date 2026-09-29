import json
import os

categories = [
    {"id": 1, "name": "Fantasi & Petualangan"},
    {"id": 2, "name": "Misteri & Thriller"},
    {"id": 3, "name": "Inspiratif & Humaniora"},
    {"id": 4, "name": "Sastra & Drama"},
    {"id": 5, "name": "Non-Fiksi & Pengembangan Diri"}
]

books_data = [
    # Tere Liye (14 books)
    {
        "id": 1,
        "categoryId": 1,
        "title": "Bumi",
        "author": "Tere Liye",
        "isbn": "9786020332956",
        "rating": 4.8,
        "totalReviews": 1420,
        "synopsis": "Namaku Raib, usiaku 15 tahun, kelas sepuluh. Aku sama saja dengan remaja lainnya, kecuali satu hal. Sesuatu yang kusimpan sendiri sejak kecil. Sesuatu yang menakjubkan: aku bisa menghilang hanya dengan menutup wajahku dengan kedua telapak tangan. Bersama Seli yang bisa mengeluarkan petir dan Ali yang sangat jenius, petualangan kami di Dunia Paralel dimulai. Di balik tembok kamar dan sudut-sudut dunia tersembunyi klan Bulan, Matahari, dan Bintang yang menyimpan rahasia besar tentang asal-usul kami.",
        "reviews": [
            {
                "id": 101,
                "bookId": 1,
                "reviewerName": "Andi Pratama",
                "userRating": 5.0,
                "comment": "Buku pembuka serial Dunia Paralel yang luar biasa imajinatif! Pengenalan karakter Raib, Seli, dan Ali sangat mengalir dan bikin penasaran lanjut ke Bulan.",
                "agreeCount": 34,
                "isAgreedByUser": False,
                "date": "14 September 2026",
                "replies": [
                    {
                        "id": 1001,
                        "reviewId": 101,
                        "replierName": "Budi Santoso",
                        "replyText": "Setuju banget! Konsep dunia paralelnya keren dan khas Tere Liye.",
                        "date": "15 September 2026"
                    }
                ]
            },
            {
                "id": 102,
                "bookId": 1,
                "reviewerName": "Siti Nurhaliza",
                "userRating": 4.5,
                "comment": "Cerita fantasi lokal yang sangat layak dibaca remaja maupun dewasa. Pertarungannya seru!",
                "agreeCount": 18,
                "isAgreedByUser": False,
                "date": "20 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 2,
        "categoryId": 1,
        "title": "Bulan",
        "author": "Tere Liye",
        "isbn": "9786020314112",
        "rating": 4.7,
        "totalReviews": 980,
        "synopsis": "Namaku Seli, dan aku bisa mengeluarkan petir dari tanganku. Ini adalah petualangan kami yang kedua di Klan Matahari. Bersama Raib dan Ali, kami harus mengikuti festival bunga matahari mekar yang sangat berbahaya. Empat kontingen terpilih harus melintasi rintangan mematikan demi menemukan bunga pertama yang mekar. Namun di balik festival suci itu, sebuah intrik politik kekuasaan sedang mengintai dan mengancam keselamatan seluruh klan.",
        "reviews": [
            {
                "id": 103,
                "bookId": 2,
                "reviewerName": "Rizky Ramadhan",
                "userRating": 5.0,
                "comment": "Festival berburunya seru banget! Lebih memacu adrenalin dibanding buku pertama.",
                "agreeCount": 21,
                "isAgreedByUser": False,
                "date": "18 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 3,
        "categoryId": 1,
        "title": "Matahari",
        "author": "Tere Liye",
        "isbn": "9786020332116",
        "rating": 4.7,
        "totalReviews": 890,
        "synopsis": "Namaku Ali, usiaku 15 tahun. Aku tahu sejak hari pertama bahwa ada yang berbeda pada duniaku. Kami bertiga mendarat di Klan Bintang, sebuah peradaban berteknologi paling mutakhir yang berada jauh di dalam perut bumi. Namun, kami disambut sebagai buronan nomor satu oleh Sekretaris Dewan Kota yang haus perang. Petualangan kali ini menuntut kecerdasan teknologi dan keberanian tanpa batas di kota berkubah kristal bawah tanah.",
        "reviews": [
            {
                "id": 104,
                "bookId": 3,
                "reviewerName": "Farhan Alamsyah",
                "userRating": 4.8,
                "comment": "Karakter Ali di buku ini benar-benar bersinar dengan kecerdasannya dan kapsul ILY!",
                "agreeCount": 15,
                "isAgreedByUser": False,
                "date": "10 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 4,
        "categoryId": 1,
        "title": "Bintang",
        "author": "Tere Liye",
        "isbn": "9786020351179",
        "rating": 4.8,
        "totalReviews": 1120,
        "synopsis": "Puncak dari pertempuran antarklan telah tiba. Pasak bumi kuno yang menahan kehancuran dunia terancam dihancurkan oleh armada perang Klan Bintang. Raib, Seli, dan Ali harus mengarungi lorong-lorong kuno penuh jebakan maut bersama teman-teman terbaik mereka untuk mencegah kepunahan peradaban. Persahabatan sejati diuji sampai batas terakhir di hadapan kekuatan tak terbayangkan.",
        "reviews": []
    },
    {
        "id": 5,
        "categoryId": 1,
        "title": "Ceros dan Batozar",
        "author": "Tere Liye",
        "isbn": "9786020385914",
        "rating": 4.6,
        "totalReviews": 750,
        "synopsis": "Buku ini menyajikan dua kisah spektakuler. Kisah pertama tentang Ceros: makhluk raksasa berkepala badak di lorong kuno bawah tanah laut Jawa yang menjaga teknologi masa lalu. Kisah kedua tentang Batozar: Sang Penjagal dari Klan Bulan yang melarikan diri dari penjara bayangan dan menyandera seluruh kota. Raib, Seli, dan Ali menemukan bahwa di balik sosok monster dan penjahat paling ditakuti, tersimpan luka mendalam dan kehormatan yang tinggi.",
        "reviews": []
    },
    {
        "id": 6,
        "categoryId": 1,
        "title": "Komet",
        "author": "Tere Liye",
        "isbn": "9786020385938",
        "rating": 4.7,
        "totalReviews": 830,
        "synopsis": "Si Tanpa Mahkota berhasil lolos dan mencari pusaka terhebat di Klan Komet. Sebuah pulau dengan tumbuhan aneh, hewan purba, dan teka-teki kuno menunggu tiga sahabat. Setiap tantangan di pulau itu hanya bisa diselesaikan dengan ketulusan hati, bukan kekuatan pukulan berdentum atau petir. Petualangan lintas kepulauan yang menguji moralitas dan kesetiaan.",
        "reviews": []
    },
    {
        "id": 7,
        "categoryId": 1,
        "title": "Komet Minor",
        "author": "Tere Liye",
        "isbn": "9786020623399",
        "rating": 4.8,
        "totalReviews": 950,
        "synopsis": "Pertarungan terakhir melawan Si Tanpa Mahkota berlangsung di Klan Komet Minor. Dengan mengendarai tombak pemburu dan melewati hutan bebatuan runcing, Ali merakit teknologi pamungkas. Di sinilah terungkap rahasia besar siapa sebenarnya pahlawan sejati dan harga pengorbanan tertinggi demi menjaga kedamaian antar-alam semesta.",
        "reviews": []
    },
    {
        "id": 8,
        "categoryId": 1,
        "title": "Selena",
        "author": "Tere Liye",
        "isbn": "9786239726201",
        "rating": 4.9,
        "totalReviews": 1300,
        "synopsis": "Masa lalu Miss Selena saat masih muda di Klan Bulan. Gadis yatim piatu miskin dari distrik terpencil yang gigih bermimpi menembus Akademi Bayangan Tingkat Tinggi (ABTT). Di sanalah persahabatannya dengan Mata dan Tazk bersemi, sebelum sebuah ambisi kelam, pengkhianatan, dan janji terlarang menuntunnya pada jalan takdir yang menyakitkan.",
        "reviews": [
            {
                "id": 105,
                "bookId": 8,
                "reviewerName": "Dewi Sartika",
                "userRating": 5.0,
                "comment": "Salah satu novel terbaik di semesta Bumi! Latar belakang Miss Selena begitu emosional dan penuh intrik.",
                "agreeCount": 42,
                "isAgreedByUser": False,
                "date": "05 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 9,
        "categoryId": 1,
        "title": "Nebula",
        "author": "Tere Liye",
        "isbn": "9786239726218",
        "rating": 4.9,
        "totalReviews": 1250,
        "synopsis": "Kelanjutan langsung kisah Selena, Mata, dan Tazk di tahun-tahun akhir kuliah mereka di ABTT. Menjelajahi tempat terlarang Klan Nebula untuk mencari cawan keabadian. Pengorbanan cinta, patah hati terdalam, dan lahirnya kekuatan bayangan yang mengubah sejarah Klan Bulan selamanya.",
        "reviews": []
    },
    {
        "id": 10,
        "categoryId": 1,
        "title": "Si Anak Kuat",
        "author": "Tere Liye",
        "isbn": "9786025734502",
        "rating": 4.6,
        "totalReviews": 640,
        "synopsis": "Kau anak paling kuat di keluarga ini, Amelia. Bukan kuat secara fisik, tapi kuat hati dan tekadmu. Kisah Amelia, anak bungsu dari lembah pedalaman Sumatra yang gigih membela tanah kelahirannya dari eksploitasi tambang pasir dan menjaga kehormatan keluarganya dengan ketulusan dan budi pekerti luhur.",
        "reviews": []
    },
    {
        "id": 11,
        "categoryId": 1,
        "title": "Hujan",
        "author": "Tere Liye",
        "isbn": "9786020324784",
        "rating": 4.9,
        "totalReviews": 2100,
        "synopsis": "Tentang melupakan... Tentang persahabatan, cinta, perpisahan, dan hujan. Mengambil latar dunia futuristik tahun 2042 pasca bencana letusan gunung purba dahsyat. Lail yang kehilangan seluruh keluarganya bertemu Esok. Di tengah teknologi modifikasi ingatan manusia, Lail harus memutuskan apakah akan menghapus seluruh memori tentang orang yang paling ia cintai demi lepas dari rasa sakit tak tertahankan.",
        "reviews": [
            {
                "id": 106,
                "bookId": 11,
                "reviewerName": "Clara Anindya",
                "userRating": 5.0,
                "comment": "Endingnya selalu bikin berlinang air mata. Plot fiksi ilmiah berpadu romansa yang begitu menyentuh hati.",
                "agreeCount": 55,
                "isAgreedByUser": False,
                "date": "22 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 12,
        "categoryId": 1,
        "title": "Pulang",
        "author": "Tere Liye",
        "isbn": "9786020822129",
        "rating": 4.8,
        "totalReviews": 1560,
        "synopsis": "Sebuah kisah tentang perjalanan seorang anak pedalaman bernama Bujang (Si Babi Hutan) yang bertransformasi menjadi algojo sekaligus pengatur strategi shadow economy keluarga Tong. Menjelajahi dunia kriminal tingkat tinggi dari Hong Kong hingga Jenewa, hingga akhirnya menemukan makna sejati dari kata 'pulang' dan berdamai dengan rasa takut serta keimanan.",
        "reviews": []
    },
    {
        "id": 13,
        "categoryId": 1,
        "title": "Pergi",
        "author": "Tere Liye",
        "isbn": "9786020822983",
        "rating": 4.8,
        "totalReviews": 1340,
        "synopsis": "Kelanjutan petualangan Bujang setelah memegang kendali Keluarga Tong. Sebuah konspirasi internasional melibatkan keluarga mafia El Padrino dari Meksiko dan penembak jitu Salonga. Bujang berkelana melintasi benua untuk mencari jawaban atas teka-teki keluarga yang disembunyikan puluhan tahun.",
        "reviews": []
    },
    {
        "id": 14,
        "categoryId": 1,
        "title": "Rembulan Tenggelam di Wajahmu",
        "author": "Tere Liye",
        "isbn": "9789793210780",
        "rating": 4.9,
        "totalReviews": 1820,
        "synopsis": "Kisah tentang Ray, konglomerat sukses yang terbaring sekarat di rumah sakit dengan hati yang penuh kepahitan dan pertanyaan kepada Tuhan. Dalam kondisi koma, sosok misterius membawanya kembali mengunjungi lima persimpangan hidupnya untuk menjawab lima pertanyaan terbesar: tentang keadilan nasib, kehilangan orang terkasih, sakit hati, dan hikmah tersembunyi di balik setiap luka.",
        "reviews": []
    },

    # Keigo Higashino (5 books)
    {
        "id": 15,
        "categoryId": 2,
        "title": "Keajaiban Toko Kelontong Namiya",
        "author": "Keigo Higashino",
        "isbn": "9786020641294",
        "rating": 4.9,
        "totalReviews": 2400,
        "synopsis": "Tiga pemuda berandal bersembunyi di sebuah toko kelontong tua terbengkalai setelah melakukan pencurian. Menjelang fajar, sepucuk surat misterius tiba-tiba jatuh melalui lubang surat. Surat itu berasal dari masa lalu, berisi permohonan nasihat hidup kepada kakek Namiya. Terhubung melintasi ruang dan waktu puluhan tahun, surat-surat balasan mereka secara ajaib mengubah jalinan takdir orang-orang yang putus asa.",
        "reviews": [
            {
                "id": 107,
                "bookId": 15,
                "reviewerName": "Hendra Wijaya",
                "userRating": 5.0,
                "comment": "Novel yang luar biasa hangat dan jenius! Plot perjumpaan waktu yang dirajut sangat rapi tanpa celah.",
                "agreeCount": 67,
                "isAgreedByUser": False,
                "date": "12 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 16,
        "categoryId": 2,
        "title": "Kesetiaan Mr. X",
        "author": "Keigo Higashino",
        "isbn": "9786020331829",
        "rating": 4.9,
        "totalReviews": 2150,
        "synopsis": "Ketika Yasuko Hanaoka membunuh mantan suaminya yang kejam demi membela diri, tetangga sebelahnya—Ishigami, seorang guru matematika jenius yang pendiam—menawarkan bantuan untuk merekayasa alibi sempurna. Menghadapi penyelidikan Detektif Kusanagi dan fisikawan jenius Manabu Yukawa (Detektif Galileo), dimulailah duel akal paling brilian dan tragis demi pengorbanan cinta terdalam.",
        "reviews": [
            {
                "id": 108,
                "bookId": 16,
                "reviewerName": "Maya Indah",
                "userRating": 5.0,
                "comment": "Masterpiece misteri pembunuhan! Duel antara dua sahabat jenius Yukawa dan Ishigami begitu memukau.",
                "agreeCount": 48,
                "isAgreedByUser": False,
                "date": "16 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 17,
        "categoryId": 2,
        "title": "Dosa Tanpa Ampun",
        "author": "Keigo Higashino",
        "isbn": "9786020633633",
        "rating": 4.7,
        "totalReviews": 1100,
        "synopsis": "Mayat seorang pria ditemukan di sebuah apartemen sewaan di Tokyo, sementara seorang wanita tewas terbakar di gubuk kumuh. Detektif Kaga Kyoichiro menemukan bahwa kedua kasus rumit ini terhubung dengan seorang sutradara teater sukses dan misteri masa lalu ibunya sendiri yang telah menghilang selama bertahun-tahun.",
        "reviews": []
    },
    {
        "id": 18,
        "categoryId": 2,
        "title": "Sihir Ranpo",
        "author": "Keigo Higashino",
        "isbn": "9786020658421",
        "rating": 4.6,
        "totalReviews": 890,
        "synopsis": "Sebuah kota kecil yang mati suri kembali bergolak ketika seorang pesulap terkenal kembali ke kampung halamannya di tengah pandemi. Pembunuhan berantai terjadi dengan motif yang penuh tipu muslihat ilusi panggung. Kematian dan teka-teki logika berbaur dalam trik sulap yang mengecoh kepolisian.",
        "reviews": []
    },
    {
        "id": 19,
        "categoryId": 2,
        "title": "Detektif Galileo",
        "author": "Keigo Higashino",
        "isbn": "9786020380582",
        "rating": 4.7,
        "totalReviews": 1420,
        "synopsis": "Kumpulan kasus pembunuhan misterius yang tampak seperti fenomena gaib: kepala yang tiba-tiba terbakar spontan, arwah yang melayang di atas danau, hingga bayangan kematian di cermin. Detektif Kusanagi meminta bantuan rekan lamanya, Profesor Manabu Yukawa, fisikawan eksentrik yang membongkar semua trik ilmiah di balik kejahatan yang tampak mustahil.",
        "reviews": []
    },

    # Andrea Hirata (7 books)
    {
        "id": 20,
        "categoryId": 3,
        "title": "Laskar Pelangi",
        "author": "Andrea Hirata",
        "isbn": "9789791227346",
        "rating": 4.9,
        "totalReviews": 3500,
        "synopsis": "Kisah sepuluh anak laskar pelangi dari keluarga miskin buruh timah di Pulau Belitong yang bersekolah di sebuah SD Muhammadiyah rapuh yang hampir roboh. Di bawah bimbingan guru penuh dedikasi Bu Muslimah dan Pak Harfan, Ikal, Lintang sang jenius cilik, Mahar sang seniman, dan kawan-kawan mengarungi perjuangan menembus keterbatasan hidup melalui keajaiban mimpi dan pendidikan.",
        "reviews": [
            {
                "id": 109,
                "bookId": 20,
                "reviewerName": "Ahmad Fauzi",
                "userRating": 5.0,
                "comment": "Buku yang mengubah cara pandang generasi Indonesia tentang perjuangan pendidikan. Tokoh Lintang sangat membekas di hati.",
                "agreeCount": 89,
                "isAgreedByUser": False,
                "date": "01 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 21,
        "categoryId": 3,
        "title": "Sang Pemimpi",
        "author": "Andrea Hirata",
        "isbn": "9789791227810",
        "rating": 4.8,
        "totalReviews": 2100,
        "synopsis": "Buku kedua tetralogi Laskar Pelangi. Ikal, Arai, dan Jimbron menjalani masa SMA di Magai, Belitong Timur. Menjadi kuli ngambat di dermaga ikan saat subuh demi membiayai sekolah, sembari mempertahankan cita-cita gila yang ditanamkan Arai: menjelajahi Eropa dan belajar di Universitas Sorbonne, Paris.",
        "reviews": []
    },
    {
        "id": 22,
        "categoryId": 3,
        "title": "Edensor",
        "author": "Andrea Hirata",
        "isbn": "9789791227025",
        "rating": 4.7,
        "totalReviews": 1780,
        "synopsis": "Buku ketiga tetralogi Laskar Pelangi. Langkah kaki Ikal dan Arai akhirnya menjejakkan benua Eropa dengan beasiswa Uni Eropa di Sorbonne. Berkelana menyusuri puluhan negara Eropa hingga Afrika dengan ransel murah, bertahan hidup dari udara beku dan rasisme, serta pencarian Ikal akan cinta sejatinya, A Ling.",
        "reviews": []
    },
    {
        "id": 23,
        "categoryId": 3,
        "title": "Maryamah Karpov",
        "author": "Andrea Hirata",
        "isbn": "9789791227452",
        "rating": 4.6,
        "totalReviews": 1400,
        "synopsis": "Penutup tetralogi Laskar Pelangi. Ikal pulang ke kampung halamannya di Belitong membawa gelar master. Namun tantangan terbesar menantinya: merakit kapal kayu bersama para sahabat untuk menembus ganasnya Selat Gaspar dan Pulau Batuan Bajak Laut demi menyelamatkan A Ling.",
        "reviews": []
    },
    {
        "id": 24,
        "categoryId": 3,
        "title": "Padang Bulan",
        "author": "Andrea Hirata",
        "isbn": "9789792257274",
        "rating": 4.6,
        "totalReviews": 1150,
        "synopsis": "Kisah Enong, gadis kecil berusia 14 tahun yang harus putus sekolah dan menjadi pendulang timah perempuan pertama di Belitong demi menghidupi ibu dan adik-adiknya setelah ayahnya tewas tertimbun tanah tambang. Sebuah monumen ketabahan hati perempuan melayu.",
        "reviews": []
    },
    {
        "id": 25,
        "categoryId": 3,
        "title": "Cinta di Dalam Gelas",
        "author": "Andrea Hirata",
        "isbn": "9789792257281",
        "rating": 4.6,
        "totalReviews": 980,
        "synopsis": "Kelanjutan kisah Padang Bulan. Maryamah bangkit melawan penindasan mantan suaminya melalui turnamen catur bergengsi di warung kopi Belitong yang selama ratusan tahun hanya didominasi laki-laki. Strategi catur digembleng dengan taktik unik khas Melayu.",
        "reviews": []
    },
    {
        "id": 26,
        "categoryId": 3,
        "title": "Orang-Orang Biasa",
        "author": "Andrea Hirata",
        "isbn": "9786022915249",
        "rating": 4.7,
        "totalReviews": 1250,
        "synopsis": "Sekelompok sahabat masa sekolah yang selalu duduk di bangku deretan belakang dan dikenal lamban merencanakan kejahatan paling aneh: merampok bank. Niat mereka bukan karena serakah, melainkan demi membiayai uang masuk Fakultas Kedokteran anak sahabat mereka yang miskin namun berotak cemerlang.",
        "reviews": []
    },

    # Sastra/Drama (10 books)
    {
        "id": 27,
        "categoryId": 4,
        "title": "Laut Bercerita",
        "author": "Leila S. Chudori",
        "isbn": "9786024246945",
        "rating": 4.9,
        "totalReviews": 3200,
        "synopsis": "Menceritakan tentang Biru Laut, mahasiswa aktivis gerakan mahasiswa era 1998 yang diculik, disiksa di tempat penyekapan rahasia, dan akhirnya ditenggelamkan ke dasar laut bersama kawan-kawannya. Bagian kedua bercerita dari sudut pandang adiknya, Asmara Jati, dan keluarga para korban penghilangan paksa yang tak pernah berhenti menunggu kepastian di depan Istana Negara.",
        "reviews": [
            {
                "id": 110,
                "bookId": 27,
                "reviewerName": "Dian Sastrowardoyo",
                "userRating": 5.0,
                "comment": "Buku yang wajib dibaca oleh setiap generasi muda Indonesia agar tidak melupakan luka sejarah bangsa.",
                "agreeCount": 73,
                "isAgreedByUser": False,
                "date": "08 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 28,
        "categoryId": 4,
        "title": "Namaku Alam",
        "author": "Leila S. Chudori",
        "isbn": "9786024248475",
        "rating": 4.8,
        "totalReviews": 1640,
        "synopsis": "Kisah tentang Segara Alam, putra dari seorang tahanan politik peristiwa 1965. Membawa stigma sebagai anak pengkhianat sejak kecil, Alam tumbuh dengan memori fotografis luar biasa dan dendam yang harus ia kendalikan sembari mencari jati diri di tengah masyarakat yang mencurigainya.",
        "reviews": []
    },
    {
        "id": 29,
        "categoryId": 4,
        "title": "Cantik Itu Luka",
        "author": "Eka Kurniawan",
        "isbn": "9786020312583",
        "rating": 4.8,
        "totalReviews": 2100,
        "synopsis": "Di satu sore di akhir pekan bulan Maret, Dewi Ayu bangkit dari kuburnya setelah dua puluh satu tahun mati. Kebangkitannya membuka kembali lembaran hitam kota Halimunda, mulai dari masa kolonial Belanda, pendudukan Jepang, hingga pembantaian komunis. Realisme magis yang menelusuri kutukan kecantikan dan tragedi kemanusiaan.",
        "reviews": [
            {
                "id": 111,
                "bookId": 29,
                "reviewerName": "Reza Rahadian",
                "userRating": 5.0,
                "comment": "Realisme magis Indonesia yang diakui dunia internasional. Kalimat pembukanya legendaris.",
                "agreeCount": 40,
                "isAgreedByUser": False,
                "date": "14 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 30,
        "categoryId": 4,
        "title": "Lelaki Harimau",
        "author": "Eka Kurniawan",
        "isbn": "9789799106964",
        "rating": 4.7,
        "totalReviews": 1350,
        "synopsis": "Margio tidak membunuh Anwar Sadat dengan pisau belati, melainkan menggigit lehernya hingga putus. Orang-orang percaya ada seekor harimau betina putih yang bersemayam di dalam tubuh pemuda itu, diwariskan dari kakeknya. Tragedi kekerasan domestik yang meledak menjadi tragedi kemanusiaan penuh dendam.",
        "reviews": []
    },
    {
        "id": 31,
        "categoryId": 4,
        "title": "Bumi Manusia",
        "author": "Pramoedya Ananta Toer",
        "isbn": "9789799731234",
        "rating": 5.0,
        "totalReviews": 4500,
        "synopsis": "Karya agung Tetralogi Buru. Mengisahkan Minke, pribumi Jawa berpendidikan Eropa yang jatuh cinta pada Annelies Mellema, putri dari Nyai Ontosoroh. Minke berhadapan langsung dengan kecongkakan hukum kolonial Hindia Belanda yang menempatkan bangsanya sebagai manusia kelas tiga tanpa hak asasi.",
        "reviews": [
            {
                "id": 112,
                "bookId": 31,
                "reviewerName": "Fajar Nugraha",
                "userRating": 5.0,
                "comment": "Karya sastra terbesar bangsa Indonesia. Karakter Nyai Ontosoroh adalah simbol ketegaran dan perlawanan!",
                "agreeCount": 112,
                "isAgreedByUser": False,
                "date": "02 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 32,
        "categoryId": 4,
        "title": "Anak Semua Bangsa",
        "author": "Pramoedya Ananta Toer",
        "isbn": "9789799731241",
        "rating": 4.9,
        "totalReviews": 2300,
        "synopsis": "Buku kedua Tetralogi Buru. Minke mulai sadar dari kekaguman butanya pada peradaban Eropa setelah berdialog dengan petani miskin seperti Trunodongso dan sahabat Tionghoanya, Khouw Ah Soe. Minke mulai menulis dalam bahasa Melayu untuk membela rakyat jelata.",
        "reviews": []
    },
    {
        "id": 33,
        "categoryId": 4,
        "title": "Jejak Langkah",
        "author": "Pramoedya Ananta Toer",
        "isbn": "9789799731258",
        "rating": 4.9,
        "totalReviews": 1950,
        "synopsis": "Buku ketiga Tetralogi Buru. Minke memasuki sekolah kedokteran STOVIA di Batavia dan menyadari kekuatan terbesar untuk membebaskan bangsanya adalah dengan berorganisasi dan mendirikan surat kabar pribumi pertama, Medan Priyayi.",
        "reviews": []
    },
    {
        "id": 34,
        "categoryId": 4,
        "title": "Rumah Kaca",
        "author": "Pramoedya Ananta Toer",
        "isbn": "9789799731265",
        "rating": 4.8,
        "totalReviews": 1800,
        "synopsis": "Penutup Tetralogi Buru. Dikisahkan dari sudut pandang Jacques Pangemanann, komisaris polisi kolonial yang ditugaskan memata-matai, mengawasi, dan membungkam gerakan Minke. Konflik batin mendalam aparat yang mengagumi korbannya namun terikat rantai birokrasi kolonial.",
        "reviews": []
    },
    {
        "id": 35,
        "categoryId": 4,
        "title": "Gadis Kretek",
        "author": "Ratih Kumala",
        "isbn": "9789792281415",
        "rating": 4.7,
        "totalReviews": 2450,
        "synopsis": "Raja rokok kretek Soeraja terbaring sekarat dan hanya memanggil satu nama perempuan yang bukan istrinya: Jeng Yah. Ketiga anak kandungnya berpacu dengan waktu menjelajahi pelosok Jawa Tengah untuk menemukan Jeng Yah, dan mengungkap rahasia racikan saus kretek legendaris serta cinta terlarang di masa pasca kemerdekaan.",
        "reviews": []
    },
    {
        "id": 36,
        "categoryId": 4,
        "title": "Filosofi Kopi",
        "author": "Dee Lestari",
        "isbn": "9789799625755",
        "rating": 4.7,
        "totalReviews": 1900,
        "synopsis": "Kumpulan cerita pendek Dee Lestari tentang Ben dan Jody yang mendirikan kedai kopi idaman. Pencarian mereka akan cangkir kopi paling sempurna membawa mereka pada kopi Tiwus di pedalaman desa, mengajarkan bahwa kesempurnaan hidup justru hadir dari menerima kepahitan dan ketidaksempurnaan.",
        "reviews": []
    },
    {
        "id": 37,
        "categoryId": 4,
        "title": "Supernova: Ksatria, Puteri, dan Bintang Jatuh",
        "author": "Dee Lestari",
        "isbn": "9786022917304",
        "rating": 4.8,
        "totalReviews": 2200,
        "synopsis": "Dua mahasiswa di Amerika Serikat, Dimas dan Reuben, berikrar menciptakan sebuah mahakarya fiksi yang memadukan sains modern, spiritualitas mistis, dan sastra romantis. Kisah fiksi mereka tentang Ferre, Rana, dan Diva secara tak kasat mata menjadi kenyataan di Jakarta.",
        "reviews": []
    },
    {
        "id": 38,
        "categoryId": 4,
        "title": "Aroma Karsa",
        "author": "Dee Lestari",
        "isbn": "9786022914631",
        "rating": 4.9,
        "totalReviews": 2750,
        "synopsis": "Jati Wesi, pemuda yang besar di Tempat Pembuangan Akhir Bantar Gebang, memiliki penciuman hidung luar biasa sensitif. Ia direkrut oleh Raras Prayagung, pemilik dinasti parfum terkemuka, untuk mencari tanaman mistis legendaris bernama Puspa Karsa yang aromanya konon mampu mengendalikan kehendak siapa pun yang menghirupnya.",
        "reviews": [
            {
                "id": 113,
                "bookId": 38,
                "reviewerName": "Anisa Triana",
                "userRating": 5.0,
                "comment": "Riset tentang wewangian dan mitologi Jawa di buku ini benar-benar luar biasa berkelas!",
                "agreeCount": 51,
                "isAgreedByUser": False,
                "date": "11 September 2026",
                "replies": []
            }
        ]
    },

    # Non-Fiksi (11 books)
    {
        "id": 39,
        "categoryId": 5,
        "title": "Filosofi Teras",
        "author": "Henry Manampiring",
        "isbn": "9786024125189",
        "rating": 4.9,
        "totalReviews": 3800,
        "synopsis": "Lebih dari 2.000 tahun lalu, mazhab filsafat Stoisisme lahir di Athena kuno. Henry Manampiring mengemas filsafat Yunani-Romawi kuno ini ke dalam konteks keseharian generasi muda Indonesia: mengatasi overthinking, kecemasan masa depan, rasa insecure di media sosial, dan menerapkan dikotomi kendali untuk mencapai kedamaian batin.",
        "reviews": [
            {
                "id": 114,
                "bookId": 39,
                "reviewerName": "Dimas Wicaksono",
                "userRating": 5.0,
                "comment": "Buku wajib untuk kaum overthinking! Konsep dikotomi kendali sangat mengubah cara saya merespons masalah.",
                "agreeCount": 94,
                "isAgreedByUser": False,
                "date": "03 September 2026",
                "replies": [
                    {
                        "id": 1002,
                        "reviewId": 114,
                        "replierName": "Taufik Hidayat",
                        "replyText": "Benar sekali, stoisisme diajarkan dengan gaya bahasa santai dan mudah dipahami.",
                        "date": "04 September 2026"
                    }
                ]
            }
        ]
    },
    {
        "id": 40,
        "categoryId": 5,
        "title": "Atomic Habits",
        "author": "James Clear",
        "isbn": "9786020633176",
        "rating": 5.0,
        "totalReviews": 5200,
        "synopsis": "Perubahan kecil yang memberikan hasil luar biasa. James Clear menjabarkan bagaimana perbaikan 1% setiap hari secara konsisten mampu melipatgandakan kesuksesan hidup. Berdasarkan 4 hukum perubahan perilaku: Menjadikannya Terlihat, Menjadikannya Menarik, Menjadikannya Mudah, dan Menjadikannya Memuaskan.",
        "reviews": [
            {
                "id": 115,
                "bookId": 40,
                "reviewerName": "Nadia Putri",
                "userRating": 5.0,
                "comment": "Buku pengembangan diri paling praktis yang pernah saya baca. Bukan sekadar motivasi kosong, tapi sistematis.",
                "agreeCount": 105,
                "isAgreedByUser": False,
                "date": "07 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 41,
        "categoryId": 5,
        "title": "Psychology of Money",
        "author": "Morgan Housel",
        "isbn": "9786025384462",
        "rating": 4.9,
        "totalReviews": 3900,
        "synopsis": "Kesuksesan finansial bukanlah tentang kecerdasan IQ atau formula matematika, melainkan bagaimana Anda mengelola emosi dan perilaku. Morgan Housel membagikan 19 cerita pendek yang mengeksplorasi cara-cara aneh orang memandang uang dan mengajarkan cara membuat keputusan keuangan jangka panjang yang bijak.",
        "reviews": []
    },
    {
        "id": 42,
        "categoryId": 5,
        "title": "Berani Tidak Disukai",
        "author": "Ichiro Kishimi & Fumitake Koga",
        "isbn": "9786020633213",
        "rating": 4.8,
        "totalReviews": 2900,
        "synopsis": "Ditulis dalam format dialog socrates antara seorang filsuf dan pemuda yang gundah, buku ini membongkar teori psikologi Alfred Adler. Mengajarkan keberanian untuk melepaskan diri dari ekspektasi orang lain, pemisahan tugas, dan menemukan kebebasan hakiki untuk bahagia di masa sekarang.",
        "reviews": []
    },
    {
        "id": 43,
        "categoryId": 5,
        "title": "Sebuah Seni untuk Bersikap Bodo Amat",
        "author": "Mark Manson",
        "isbn": "9786020523316",
        "rating": 4.7,
        "totalReviews": 3400,
        "synopsis": "Pendekatan waras demi menjalani hidup yang baik. Mark Manson mendobrak budaya kepositifan toksik dengan kejujuran tanpa tedeng aling-aling: hidup ini penuh penderitaan dan masalah, kuncinya bukan menghindari masalah, melainkan memilih masalah mana yang layak kita perjuangkan.",
        "reviews": []
    },
    {
        "id": 44,
        "categoryId": 5,
        "title": "Bicara Itu Ada Seninya",
        "author": "Oh Su Hyang",
        "isbn": "9786024291167",
        "rating": 4.7,
        "totalReviews": 2100,
        "synopsis": "Pakar komunikasi terkemuka Korea Selatan Oh Su Hyang mengupas rahasia komunikasi efektif yang memikat hati pendengar. Membahas teknik vokal, storytelling, negosiasi, hingga cara mendengarkan dengan empati untuk kesuksesan karier dan hubungan sosial.",
        "reviews": []
    },
    {
        "id": 45,
        "categoryId": 5,
        "title": "Grit: Kekuatan Passion dan Kegigihan",
        "author": "Angela Duckworth",
        "isbn": "9786020620862",
        "rating": 4.8,
        "totalReviews": 1750,
        "synopsis": "Mengapa bakat alami bukanlah penentu utama kesuksesan? Psikolog Angela Duckworth membuktikan melalui riset ilmiah puluhan tahun bahwa kombinasi antara gairah jangka panjang (passion) dan ketekunan pantang menyerah (grit) adalah faktor sejati pencapaian puncak prestasi manusia.",
        "reviews": []
    },
    {
        "id": 46,
        "categoryId": 5,
        "title": "Ikigai: Rahasia Hidup Bahagia dan Panjang Umur",
        "author": "Hector Garcia & Francesc Miralles",
        "isbn": "9786026714619",
        "rating": 4.8,
        "totalReviews": 2300,
        "synopsis": "Menyelami kearifan lokal penduduk desa Ogimi di Okinawa, Jepang—wilayah dengan penduduk berumur seratus tahun terbanyak di dunia. Menemukan titik temu antara apa yang Anda cintai, keahlian Anda, apa yang dibutuhkan dunia, dan apa yang bisa menghasilkan nafkah bagi Anda.",
        "reviews": []
    },
    {
        "id": 47,
        "categoryId": 5,
        "title": "Sapiens: Riwayat Singkat Umat Manusia",
        "author": "Yuval Noah Harari",
        "isbn": "9786024244163",
        "rating": 5.0,
        "totalReviews": 4900,
        "synopsis": "Seratus ribu tahun lalu, setidaknya ada enam spesies manusia mendiami bumi. Hari ini hanya tersisa satu: Homo sapiens. Sejarawan Yuval Noah Harari menuturkan kisah spektakuler bagaimana kera tak berarti berevolusi menjadi penguasa planet bumi berkat Revolusi Kognitif, Revolusi Pertanian, dan Revolusi Sains.",
        "reviews": [
            {
                "id": 116,
                "bookId": 47,
                "reviewerName": "Gilang Ramadhan",
                "userRating": 5.0,
                "comment": "Buku paling membuka wawasan tentang sejarah peradaban manusia dan mitos tatanan imajiner.",
                "agreeCount": 88,
                "isAgreedByUser": False,
                "date": "10 September 2026",
                "replies": []
            }
        ]
    },
    {
        "id": 48,
        "categoryId": 5,
        "title": "Manusia Setengah Salmon",
        "author": "Raditya Dika",
        "isbn": "9789797805319",
        "rating": 4.6,
        "totalReviews": 2100,
        "synopsis": "Kumpulan komedi autobiografi Raditya Dika yang menertawakan proses perpindahan dalam hidup: pindah rumah, pindah hubungan cinta, hingga pergeseran cara pandang kedewasaan, diibaratkan seperti perjuangan ikan salmon yang bermigrasi melawan arus demi melanjutkan kehidupan.",
        "reviews": []
    },
    {
        "id": 49,
        "categoryId": 5,
        "title": "Kambing Jantan",
        "author": "Raditya Dika",
        "isbn": "9789797800598",
        "rating": 4.6,
        "totalReviews": 2600,
        "synopsis": "Buku catatan harian mahasiswa bodoh yang menjadi pelopor buku komedi personal di Indonesia. Pengalaman kocak dan absurd Raditya Dika selama menempuh studi sarjana di Adelaide, Australia, berhadapan dengan masalah pacaran jarak jauh, teman sekamar eksentrik, dan kebingungan masa muda.",
        "reviews": []
    }
]

# Set coverImg URL standard
for b in books_data:
    b["coverImg"] = f"https://covers.openlibrary.org/b/isbn/{b['isbn']}-L.jpg"

output_dir = "app/src/main/assets"
os.makedirs(output_dir, exist_ok=True)
output_path = os.path.join(output_dir, "books.json")

data = {
    "categories": categories,
    "books": books_data
}

with open(output_path, "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print(f"Successfully generated {len(books_data)} books and {len(categories)} categories to {output_path}")
