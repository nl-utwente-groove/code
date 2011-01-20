<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="append-1-list-1" role="graph" edgeids="false" edgemode="directed">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n10313"/>
        <node id="n10314"/>
        <node id="n10317"/>
        <node id="n10316"/>
        <node id="n10311"/>
        <node id="n10315"/>
        <node id="n10312"/>
        <edge from="n10313" to="n10313">
            <attr name="label">
                <string>control</string>
            </attr>
        </edge>
        <edge from="n10315" to="n10317">
            <attr name="label">
                <string>list</string>
            </attr>
        </edge>
        <edge from="n10317" to="n10316">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n10317" to="n10311">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
        <edge from="n10313" to="n10313">
            <attr name="label">
                <string>append</string>
            </attr>
        </edge>
        <edge from="n10313" to="n10315">
            <attr name="label">
                <string>caller</string>
            </attr>
        </edge>
        <edge from="n10314" to="n10314">
            <attr name="label">
                <string>4</string>
            </attr>
        </edge>
        <edge from="n10315" to="n10315">
            <attr name="label">
                <string>root</string>
            </attr>
        </edge>
        <edge from="n10312" to="n10312">
            <attr name="label">
                <string>5</string>
            </attr>
        </edge>
        <edge from="n10313" to="n10314">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n10313" to="n10317">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
        <edge from="n10316" to="n10312">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
        <edge from="n10311" to="n10311">
            <attr name="label">
                <string>3</string>
            </attr>
        </edge>
    </graph>
</gxl>
