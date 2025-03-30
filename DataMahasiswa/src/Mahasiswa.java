import java.util.Date;

public class Mahasiswa {
    private String nim;
    private String nama;
    private String jenisKelamin;
    private String tanggalLahir;

    public Mahasiswa(String nim, String nama, String jenisKelamin, String tanggalLahir) {
        this.nim = nim;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.tanggalLahir = tanggalLahir;
    }

    public void setNim(String nim) { this.nim = nim; }

    public void setNama(String nama) { this.nama = nama; }

    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }

    public void setTanggalLahir(String tanggalLahir) { this.tanggalLahir = tanggalLahir; }

    public String getNim() { return this.nim; }

    public String getNama() { return this.nama; }

    public String getJenisKelamin() { return this.jenisKelamin; }

    public String getTanggalLahir() { return this.tanggalLahir; }
}