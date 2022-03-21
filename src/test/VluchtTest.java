package test;

import main.domeinLaag.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class VluchtTest {

	static LuchtvaartMaatschappij lvm ;
	static Fabrikant f1; 
	static VliegtuigType vtt1; 
	static Vliegtuig vt1;
	static Luchthaven lh1, lh2;
	static Vlucht vl1, vl2; 

	@BeforeEach
	public void initialize() {
		try {
			lvm = new LuchtvaartMaatschappij("NLM");
			f1 = new Fabrikant("Airbus","G. Dejenelle");
			vtt1 = f1.creeervliegtuigtype("A-200", 140);
			Calendar datum = Calendar.getInstance();
			datum.set(2000, 01, 01);
			vt1 = new Vliegtuig(lvm, vtt1, "Luchtbus 100", datum);
			Land l1 = new Land("Nederland", 31);
			Land l2 = new Land("België", 32);
			lh1 = new Luchthaven("Schiphol", "ASD", true, l1);
			lh2 = new Luchthaven("Tegel", "TEG", true, l2);
			Calendar vertr = Calendar.getInstance();
			vertr.set(2020, 03, 30, 14, 15, 0);
			Calendar aank = Calendar.getInstance();
			aank.set(2020, 03, 30, 15, 15, 0);
			vl1 = new Vlucht(vt1, lh1, lh2, vertr, aank );
			vertr.set(2020, 4, 1, 8, 15, 0);
			aank.set(2020, 4, 1, 9, 15, 0);
			vl2 = new Vlucht(vt1, lh1, lh2, vertr, aank );
		} catch (Exception e){
			String errorMessage =  "Exception: " + e.getMessage();
			System.out.println(errorMessage); 
		}
	}

	/**
	 * Business rule:
	 * De bestemming moet verschillen van het vertrekpunt van de vlucht.
	 */
	
	@Test
	public void test_1_BestemmingMagNietGelijkZijnAanVertrek_False() {
		Vlucht vlucht = new Vlucht();
		try {
			vlucht.zetVliegtuig(vt1);
			vlucht.zetVertrekpunt(lh1);
			Luchthaven bestemming = vlucht.getBestemming();
			assertTrue(bestemming == null);
			vlucht.zetBestemming(lh1);
			// De test zou niet verder mogen komen: er moet al een exception gethrowd zijn.
			bestemming = vlucht.getBestemming();
			assertFalse(bestemming.equals(lh1));
		}
		catch(IllegalArgumentException e) {
			Luchthaven bestemming = vlucht.getBestemming();
			assertFalse(bestemming.equals(lh1));
		}
	}

	@Test
	public void test_2_BestemmingMagNietGelijkZijnAanVertrek_True() {
		Vlucht vlucht = new Vlucht();
		Luchthaven bestemming;
		try {
			vlucht.zetVliegtuig(vt1);
			vlucht.zetVertrekpunt(lh2);
			bestemming = vlucht.getBestemming();
			assertTrue(bestemming == null);
			vlucht.zetBestemming(lh1);
			bestemming = vlucht.getBestemming();
			assertTrue(bestemming.equals(lh1));
		}
		catch(IllegalArgumentException e) {
			bestemming = vlucht.getBestemming();
			assertTrue(bestemming.equals(lh1));
		}
	}
	/**
	 * Business rule:
	 * De vertrektijd en aankomsttijd moeten geldig zijn (dus een bestaande dag/uur/minuten combinatie
	 * aangeven) en in de toekomst liggen.
	 */

	@Test
	public void test_3_AankomstTijdNietIngevuld_False() {
		// FOUTMELDING "geen geldige datum/tijd"
		Vlucht vlucht = new Vlucht();
		Luchthaven bestemming;
		try {
			vlucht.zetVliegtuig(vt1);
			vlucht.zetVertrekpunt(lh1);
			vlucht.zetBestemming(lh2);
			Calendar vertrek = Calendar.getInstance();
			vertrek.set(2025, 9, 31, 24, 0);
			assertFalse(vlucht.getAankomstTijd() == null);
		}catch(IllegalArgumentException e){
			assertFalse(vlucht.getAankomstTijd() == null);
		}
	}

	@Test
	public void test_5_RegistreerVluchtMetGeldigeGegevens_True() {
		// GEEN foutmelding
		Vlucht vlucht = new Vlucht();
		try {
			Calendar vertrek = Calendar.getInstance();
			Calendar aankomst = Calendar.getInstance();
			vertrek.set(2025, Calendar.SEPTEMBER, 30, 12, 0);
			aankomst.set(2025, Calendar.SEPTEMBER, 30, 12, 0);

			vlucht.zetVliegtuig(vt1);
			vlucht.zetVertrekpunt(lh1);
			vlucht.zetBestemming(lh2);
			//vlucht.zetVertrekTijd(vertrek);
			//vlucht.zetAankomstTijd(aankomst);

			vlucht = new Vlucht(vt1, lh1, lh2, vertrek, aankomst);
			assertTrue(vlucht.getAankomstTijd() != null);
			assertTrue(vlucht.getVertrekTijd() != null);
		}catch(IllegalArgumentException e){
			assertTrue(vlucht.getAankomstTijd() != null);
			assertTrue(vlucht.getVertrekTijd() != null);
		}
	}

	@Test
	public void test_6_Vertrektijd1MinuutInHetVerleden_False() {
		// FOUTMELDING "tijd in het verleden"
		Vlucht vlucht = new Vlucht();
		try {
			vlucht.zetVliegtuig(vt1);
			vlucht.zetVertrekpunt(lh1);
			vlucht.zetBestemming(lh2);
		}catch(IllegalArgumentException e){

		}
	}

	@Test
	public void test_7_VertrekEnAankomstTijdInHetVerleden_False() {
		// FOUTMELDING 2X "tijd in het verleden"
		try {

		}catch(IllegalArgumentException e){

		}
	}

	@Test
	public void test_8_RegistreerVluchtVanNuTotOver1Minuut_True() {
		// GEEN foutmelding
		try {

		}catch(IllegalArgumentException e){

		}
	}

	/**
	 * Business rule:
	 * De aankomsttijd moet na de vertrektijd liggen.
	 */

	@Test
	public void test_9_VertrektijdNaAankomsttijd_False() {
		// FOUTMELDING "vertrektijd < aankomsttijd"
		try {

		}catch(IllegalArgumentException e){

		}
	}

	@Test
	public void test_10_VertrektijdVoorAankomsttijd_True() {
		// GEEN foutmelding
		try {

		}catch(IllegalArgumentException e){

		}
	}

	/**
	 * Business rule:
	 * Een vliegtuig kan maar voor één vlucht tegelijk gebruikt worden.
	 */

	@Test
	public void test_11_OverlappendeVluchtVertrektijdBinnen_False() {
		// FOUTMELDING "overlappende vlucht"
		Vlucht vlucht = new Vlucht();
		try {
			vlucht.zetVliegtuig(vt1);
			Calendar vertrek = Calendar.getInstance();
			Calendar aankomst = Calendar.getInstance();
			vertrek.set(2020, 03, 30, 14, 35, 10);
			aankomst.set(2020, 03, 30, 16, 36, 10);
			vlucht.zetVertrekTijd(vertrek);
			assertTrue(vlucht.getVertrekTijd() == null);
			vlucht.zetAankomstTijd(aankomst);

		} catch (VluchtException e) {
			assertEquals("main.domeinLaag.VluchtException: Vliegtuig reeds bezet op Thu Apr 30 14:35:10 CEST 2020", e.toString());
		}
	}

	@Test
	public void test_12_OverlappendeVluchtAankomsttijdBinnen_False() {
		// FOUTMELDING "overlappende vlucht"
		Vlucht vlucht = new Vlucht();
		try {
			vlucht.zetVliegtuig(vt1);
			Calendar vertrek = Calendar.getInstance();
			Calendar aankomst = Calendar.getInstance();
			vertrek.set(2020, 03, 30, 11, 36, 10);
			aankomst.set(2020, 03, 30, 14, 30, 10);
			vlucht.zetVertrekTijd(vertrek);
			vlucht.zetAankomstTijd(aankomst);

			assertEquals("main.domeinLaag.VluchtException: Vliegtuig reeds bezet op Thu Apr 30 14:30:10 CEST 2020", vlucht.getAankomstTijd());



		}catch(IllegalArgumentException | VluchtException e) {
			assertEquals("main.domeinLaag.VluchtException: Vliegtuig reeds bezet op Thu Apr 30 14:30:10 CEST 2020", e.toString());
		}
	}

	@Test
	public void test_13_OverlappendeVluchtOverBestaandeHeen_False() {
		// FOUTMELDING "overlappende vlucht"
		try {

		}catch(IllegalArgumentException e){

		}
	}

	@Test
	public void test_14_RegistreerVluchtBuitenBestaandeVlucht_True() {
		// GEEN foutmelding
		try {

		}catch(IllegalArgumentException e){

		}
	}

	/**
	 * Business rule:
	 * Een vlucht mag alleen geaccepteerd worden als de volgende gegevens zijn vastgelegd: vliegtuig,
	 * vertrekpunt, bestemming, vertrektijd, aankomsttijd.
	 */

	@Test
	public void test_15_VliegtuigOngeldig_False() {
		// FOUTMELDING "Vliegtuig ongeldig"
		try {

		}catch(IllegalArgumentException e){

		}
	}

	@Test
	public void test_16_VertrekpuntOngeldig_False() {
		// FOUTMELDING NA OK "Vertrekpunt ongeldig"
		try {

		}catch(IllegalArgumentException e){

		}
	}

	@Test
	public void test_17_BestemmingOngeldig_False() {
		// FOUTMELDING NA OK "Bestemming ongeldig"
		try {

		}catch(IllegalArgumentException e){

		}
	}

	@Test
	public void test_18_VertrektijdOngeldig_False() {
		// FOUTMELDING NA OK "Vetrektijd ongeldig"
		try {

		}catch(IllegalArgumentException e){

		}
	}

	@Test
	public void test_19_AankomsttijdOngeldig_False() {
		// FOUTMELDING NA OK "Aankomsttijd ongeldig"
		try {

		}catch(IllegalArgumentException e){

		}
	}

	@Test
	public void test_20_CompleetGeldigeVluchtGeregistreerd() {
		// GEEN FOUTMELDING NA OK
		try {

		}catch(IllegalArgumentException e){

		}
	}
	/**
	 * Business rule:
	 * xxx
	 */
	
		
}
