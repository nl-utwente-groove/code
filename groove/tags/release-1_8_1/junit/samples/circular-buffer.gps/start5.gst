<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n100"/>
        <node id="n101"/>
        <node id="n102"/>
        <node id="n97"/>
        <node id="n98"/>
        <node id="n99"/>
        <edge from="n99" to="n98">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
        <edge from="n99" to="n102">
            <attr name="label">
                <string>first</string>
            </attr>
        </edge>
        <edge from="n101" to="n100">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n101" to="n101">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge from="n100" to="n98">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n99" to="n99">
            <attr name="label">
                <string>Buffer</string>
            </attr>
        </edge>
        <edge from="n100" to="n100">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge from="n102" to="n97">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n102" to="n102">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge from="n97" to="n97">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge from="n98" to="n102">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n98" to="n98">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge from="n97" to="n101">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
    </graph>
</gxl>
